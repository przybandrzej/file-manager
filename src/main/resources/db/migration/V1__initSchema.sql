CREATE TABLE directory (
    id bigint NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    name character varying(255),
    parent_id bigint
);

CREATE SEQUENCE directory_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE directory_id_seq OWNED BY directory.id;

CREATE TABLE resource_file (
    id bigint NOT NULL,
    created timestamp without time zone,
    extension character varying(255),
    generated_name character varying(255),
    modified timestamp without time zone,
    original_name character varying(255),
    backed_up boolean,
    parent_directory_id bigint
);

CREATE SEQUENCE resource_file_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE resource_file_id_seq OWNED BY resource_file.id;

ALTER TABLE ONLY directory ALTER COLUMN id SET DEFAULT nextval('directory_id_seq'::regclass);

ALTER TABLE ONLY resource_file ALTER COLUMN id SET DEFAULT nextval('resource_file_id_seq'::regclass);

SELECT pg_catalog.setval('directory_id_seq', 1, false);

SELECT pg_catalog.setval('resource_file_id_seq', 1, false);

ALTER TABLE ONLY directory
    ADD CONSTRAINT directory_pkey PRIMARY KEY (id);

ALTER TABLE ONLY resource_file
    ADD CONSTRAINT resource_file_pkey PRIMARY KEY (id);

ALTER TABLE ONLY directory
    ADD CONSTRAINT fkeqcjnap90heiiqqfon2shtao0 FOREIGN KEY (parent_id) REFERENCES directory(id);

ALTER TABLE ONLY resource_file
    ADD CONSTRAINT fkre37s8ib77jm8rs2ux2i9s39r FOREIGN KEY (parent_directory_id) REFERENCES directory(id);
