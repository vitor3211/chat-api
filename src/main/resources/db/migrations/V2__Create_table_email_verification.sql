CREATE TABLE IF NOT EXISTS public.email_verification
(
    email_id uuid NOT NULL,
    email character varying(255) COLLATE pg_catalog."default" NOT NULL,
    expires timestamp(6) without time zone NOT NULL,
    token character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT email_verification_pkey PRIMARY KEY (email_id)
    );
