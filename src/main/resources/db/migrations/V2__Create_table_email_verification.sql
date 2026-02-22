CREATE TABLE IF NOT EXISTS public.email_verification
(
    email_id uuid NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    token varchar(255) NOT NULL UNIQUE,
    expires timestamp(6) NOT NULL,
    CONSTRAINT fk_email_verification_user
    FOREIGN KEY(user_id)
    REFERENCES public.users(id)
    ON DELETE CASCADE
    );