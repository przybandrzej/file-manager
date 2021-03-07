CREATE TABLE public.directory (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    name character varying(255),
    parent_id bigint
);

ALTER TABLE public.directory OWNER TO file_manager;

CREATE SEQUENCE public.directory_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.directory_id_seq OWNER TO file_manager;

ALTER SEQUENCE public.directory_id_seq OWNED BY public.directory.id;

CREATE TABLE public.resource_file (
    id bigint NOT NULL,
    created timestamp without time zone,
    extension character varying(255),
    generated_name character varying(255),
    modified timestamp without time zone,
    original_name character varying(255),
    backed_up boolean,
    parent_directory_id bigint
);

ALTER TABLE public.resource_file OWNER TO file_manager;

CREATE SEQUENCE public.resource_file_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.resource_file_id_seq OWNER TO file_manager;

ALTER SEQUENCE public.resource_file_id_seq OWNED BY public.resource_file.id;

ALTER TABLE ONLY public.directory ALTER COLUMN id SET DEFAULT nextval('public.directory_id_seq'::regclass);

ALTER TABLE ONLY public.resource_file ALTER COLUMN id SET DEFAULT nextval('public.resource_file_id_seq'::regclass);

SELECT pg_catalog.setval('public.directory_id_seq', 1, false);

SELECT pg_catalog.setval('public.resource_file_id_seq', 1, false);

ALTER TABLE ONLY public.directory
    ADD CONSTRAINT directory_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.resource_file
    ADD CONSTRAINT resource_file_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.directory
    ADD CONSTRAINT fkeqcjnap90heiiqqfon2shtao0 FOREIGN KEY (parent_id) REFERENCES public.directory(id);

ALTER TABLE ONLY public.resource_file
    ADD CONSTRAINT fkre37s8ib77jm8rs2ux2i9s39r FOREIGN KEY (parent_directory_id) REFERENCES public.directory(id);
