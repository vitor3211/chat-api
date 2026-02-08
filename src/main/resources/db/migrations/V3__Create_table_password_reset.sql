CREATE TABLE IF NOT EXISTS public.password_reset
(
    password_id uuid NOT NULL,
    email character varying(255) COLLATE pg_catalog."default" NOT NULL,
    expires timestamp(6) without time zone NOT NULL,
    CONSTRAINT password_reset_pkey PRIMARY KEY (password_id)
    )

ALTER TABLE IF EXISTS public.password_reset
    OWNER to postgres;
