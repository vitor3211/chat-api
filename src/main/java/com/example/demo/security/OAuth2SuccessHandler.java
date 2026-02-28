package com.example.demo.security;

import com.example.demo.dto.response.AuthorizationResponse;
import com.example.demo.entity.tokens.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserProvider;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(oidcUser.getFullName());
                    newUser.setUserRole(UserRole.USER);
                    newUser.setUserProvider(UserProvider.GOOGLE);
                    newUser.setCreationDate(LocalDateTime.now());
                    newUser.setVerified(true);
                    newUser.setImageProfileUrl(oidcUser.getPicture());
                    return userRepository.save(newUser);
                });

        String token = tokenService.generateToken(user);
        RefreshToken refreshToken = tokenService.generateRefreshToken(user);
        AuthorizationResponse authResponse = new AuthorizationResponse(token, refreshToken.getToken(), 900L);

        response.setContentType("text/html;charset=UTF-8");
        String html = "<!DOCTYPE html><html><head><title>Autenticando...</title></head><body>" +
                "<script type=\"text/javascript\">" +
                "    const data = {" +
                "        token: '" + token + "'," +
                "        refreshToken: '" + refreshToken.getToken() + "'," +
                "        expiresIn: 900" +
                "    };" +
                "    window.opener.postMessage(data, 'http://localhost:3000');" +
                "    window.close();" +
                "</script>" +
                "</body></html>";

        response.getWriter().write(html);
        response.getWriter().flush();
    }
}
