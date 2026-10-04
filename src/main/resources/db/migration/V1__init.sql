CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE accounts
(
    id         UUID         NOT NULL,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

CREATE TABLE file_meta_data_table
(
    id                UUID         NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename   VARCHAR(255) NOT NULL,
    category          VARCHAR(255),
    content_type      VARCHAR(255) NOT NULL,
    size              BIGINT       NOT NULL,
    account_id        UUID         NOT NULL,
    created_at        TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_file_meta_data_table PRIMARY KEY (id)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE accounts
    ADD CONSTRAINT uc_accounts_email UNIQUE (email);

ALTER TABLE file_meta_data_table
    ADD CONSTRAINT uc_file_meta_data_table_stored_filename UNIQUE (stored_filename);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);