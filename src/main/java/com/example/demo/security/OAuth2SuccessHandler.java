package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserProvider;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(JwtEncoder jwtEncoder, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
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
                    return userRepository.save(newUser);
                });

        List<String> roles = user.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .toList();

        var claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.getId().toString())
                .claim("roles", roles)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600))
                .build();

        String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        response.setContentType("application/json");
        response.getWriter().write("{\"token\":\"" + jwtValue + "\"}");
    }
}
