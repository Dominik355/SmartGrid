DROP TABLE IF EXISTS DATASET_QUANTITY_JOINS;
DROP TABLE IF EXISTS IMPLEMENTED_TABLE;
DROP TABLE IF EXISTS RECORD;
DROP TABLE IF EXISTS QUANTITY_DETAIL_OBIS;
DROP TABLE IF EXISTS QUANTITY_DETAIL;
DROP TABLE IF EXISTS MEASUREMENT_HEADER;
DROP TABLE IF EXISTS MEASUREMENT;
DROP TABLE IF EXISTS DATASET;

CREATE TABLE IF NOT EXISTS DATASET (
    id bigint PRIMARY KEY,
    version int NOT NULL,
    name varchar(255) NOT NULL,
    reference_device_id bigint,
    device_id bigint,
    quantity_type_name varchar(127) NOT NULL,
    implemented_table_id bigint
);

CREATE TABLE IF NOT EXISTS QUANTITY_DETAIL (
    id bigint PRIMARY KEY,
    name varchar(255),
    unit varchar(255),
    is_obis_name boolean
);

CREATE TABLE IF NOT EXISTS QUANTITY_DETAIL_OBIS (
    id bigint PRIMARY KEY,
    A_value varchar(10),
    B_value varchar(10),
    C_value varchar(10),
    D_value varchar(10),
    E_value varchar(10),
    F_value varchar(10)
);

CREATE TABLE IF NOT EXISTS DATASET_QUANTITY_JOINS (
    dataset_id bigint references DATASET (id),
    quantity_detail_id bigint references QUANTITY_DETAIL (id)
);

CREATE TABLE IF NOT EXISTS IMPLEMENTED_TABLE (
    id bigint PRIMARY KEY,
    table_name varchar(255),
    class_name varchar(255),
    creation_date_time timestamp,
    version integer,
    dataset_id bigint NOT NULL references DATASET (id)
);

CREATE TABLE IF NOT EXISTS MEASUREMENT (
    id bigint PRIMARY KEY,
    reference_device_id bigint,
    device_id bigint,
    dataset_id bigint references DATASET (id),
    source_filename varchar(255) NOT NULL,
    source_file_id bigint,
    measurement_type varchar(255) NOT NULL,
    measurement_type_by_date varchar(255) NOT NULL,
    date_time_from timestamp NOT NULL,
    date_time_to timestamp NOT NULL,
    records_type varchar(255) NOT NULL,
    info_type varchar(255),
    records_count int,
    frequency_minutes int
);

CREATE TABLE IF NOT EXISTS MEASUREMENT_HEADER (
    measurement_id bigint NOT NULL references MEASUREMENT (id),
    key varchar(255) NOT NULL,
    value varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS RECORD (
    value double precision NOT NULL,
    date_time_from timestamp NOT NULL,
    date_time_to timestamp NOT NULL,
    dataset_id bigint NOT NULL references DATASET (id),
    quantity_details_id bigint NOT NULL references QUANTITY_DETAIL (id)
);

CREATE SEQUENCE IF NOT EXISTS MEASUREMENT_SEQ START 1;
CREATE SEQUENCE IF NOT EXISTS DATASET_SEQ START 1;
CREATE SEQUENCE IF NOT EXISTS QUANTITY_DETAIL_SEQ START 1;
CREATE SEQUENCE IF NOT EXISTS IMPLEMENTED_TABLE_SEQ START 1;