CREATE TABLE refresh_token (
    uuid UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    expires TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    creation_date TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY(user_id) REFERENCES tb_users(id)
    );

