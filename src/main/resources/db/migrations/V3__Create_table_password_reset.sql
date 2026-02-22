CREATE TABLE IF NOT EXISTS public.password_reset
(
    password_id uuid NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    token varchar(255) NOT NULL UNIQUE,
    expires timestamp(6) NOT NULL,
    CONSTRAINT fk_password_reset_user
    FOREIGN KEY(user_id)
    REFERENCES public.users(id)
    ON DELETE CASCADE
    );