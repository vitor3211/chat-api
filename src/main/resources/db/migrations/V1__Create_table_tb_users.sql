CREATE TABLE IF NOT EXISTS public.tb_users
(
    id uuid NOT NULL,
    creation_date timestamp(6) without time zone NOT NULL,
    email character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    password character varying(255) COLLATE pg_catalog."default",
    user_provider character varying(255) COLLATE pg_catalog."default" NOT NULL,
    user_role character varying(255) COLLATE pg_catalog."default" NOT NULL,
    verified boolean NOT NULL,
    CONSTRAINT tb_users_pkey PRIMARY KEY (id),
    CONSTRAINT ukgrd22228p1miaivbn9yg178pm UNIQUE (email),
    CONSTRAINT ukt5xrb42j5hy9f23bwrf2tlpgu UNIQUE (name),
    CONSTRAINT tb_users_user_provider_check CHECK (user_provider::text = ANY (ARRAY['LOCAL'::character varying, 'GOOGLE'::character varying]::text[])),
    CONSTRAINT tb_users_user_role_check CHECK (user_role::text = ANY (ARRAY['ADMIN'::character varying, 'USER'::character varying]::text[]))
    );
