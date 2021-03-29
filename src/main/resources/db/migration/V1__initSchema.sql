CREATE TABLE directory
(
    id        bigint NOT NULL,
    created   timestamp without time zone,
    modified  timestamp without time zone,
    name      character varying(255),
    parent_id bigint
);
CREATE SEQUENCE directory_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE directory_id_seq OWNED BY directory.id;

CREATE TABLE resource_file
(
    id                  bigint NOT NULL,
    backed_up           boolean,
    back_up             boolean,
    created             timestamp without time zone,
    extension           character varying(255),
    generated_name      character varying(255),
    modified            timestamp without time zone,
    original_name       character varying(255),
    size                bigint,
    size_unit           character varying(255),
    parent_directory_id bigint NOT NULL
);
CREATE SEQUENCE resource_file_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE resource_file_id_seq OWNED BY resource_file.id;

CREATE TABLE resource_file_link
(
    id             bigint NOT NULL,
    link_name      character varying(255),
    child_file_id  bigint NOT NULL,
    parent_file_id bigint NOT NULL
);
CREATE SEQUENCE resource_file_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE resource_file_link_id_seq OWNED BY resource_file_link.id;

CREATE TABLE resource_file_tag
(
    resource_file_id bigint NOT NULL,
    tag_id           bigint NOT NULL
);
ALTER TABLE ONLY directory
    ALTER COLUMN id SET DEFAULT nextval('directory_id_seq'::regclass);

ALTER TABLE ONLY resource_file
    ALTER COLUMN id SET DEFAULT nextval('resource_file_id_seq'::regclass);

ALTER TABLE ONLY resource_file_link
    ALTER COLUMN id SET DEFAULT nextval('resource_file_link_id_seq'::regclass);

SELECT pg_catalog.setval('directory_id_seq', 1, false);


SELECT pg_catalog.setval('resource_file_id_seq', 1, false);


SELECT pg_catalog.setval('resource_file_link_id_seq', 1, false);

ALTER TABLE ONLY directory
    ADD CONSTRAINT directory_pkey PRIMARY KEY (id);

ALTER TABLE ONLY resource_file_link
    ADD CONSTRAINT resource_file_link_pkey PRIMARY KEY (id);

ALTER TABLE ONLY resource_file
    ADD CONSTRAINT resource_file_pkey PRIMARY KEY (id);

ALTER TABLE ONLY resource_file_tag
    ADD CONSTRAINT resource_file_tag_pkey PRIMARY KEY (resource_file_id, tag_id);

ALTER TABLE ONLY resource_file_tag
    ADD CONSTRAINT fkajwa2hnevyox64eob06fo70lv FOREIGN KEY (resource_file_id) REFERENCES resource_file (id);

ALTER TABLE ONLY directory
    ADD CONSTRAINT fkeqcjnap90heiiqqfon2shtao0 FOREIGN KEY (parent_id) REFERENCES directory (id);

ALTER TABLE ONLY resource_file_tag
    ADD CONSTRAINT fkkpn1t45i38wvh4wgmfs1r7ej5 FOREIGN KEY (tag_id) REFERENCES directory (id);

ALTER TABLE ONLY resource_file_link
    ADD CONSTRAINT fkr3s2vd7hrtdx2q415x2fw5hg4 FOREIGN KEY (child_file_id) REFERENCES resource_file (id);

ALTER TABLE ONLY resource_file
    ADD CONSTRAINT fkre37s8ib77jm8rs2ux2i9s39r FOREIGN KEY (parent_directory_id) REFERENCES directory (id);

ALTER TABLE ONLY resource_file_link
    ADD CONSTRAINT fkrqc36sikejg3utdnrsx9mmahj FOREIGN KEY (parent_file_id) REFERENCES resource_file (id);
