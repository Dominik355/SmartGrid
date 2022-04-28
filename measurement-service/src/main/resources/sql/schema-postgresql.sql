--DROP TABLE IF EXISTS DATASET_QUANTITY_JOINS;
--DROP TABLE IF EXISTS IMPLEMENTED_TABLE;
DROP TABLE IF EXISTS RECORDS;
--DROP TABLE IF EXISTS dataset_quantity_joins;
--DROP TABLE IF EXISTS QUANTITY_DETAIL;
--DROP TABLE IF EXISTS MEASUREMENT_HEADER;
--DROP TABLE IF EXISTS MEASUREMENT;
--DROP TABLE IF EXISTS DATASET;

CREATE TABLE IF NOT EXISTS dataset (
    id bigint PRIMARY KEY,
    version int NOT NULL,
    name varchar(255) NOT NULL,
    reference_device_id bigint,
    device_id varchar(32),
    quantity_type_name varchar(127) NOT NULL,
    frequency_in_seconds integer
);

CREATE SEQUENCE IF NOT EXISTS dataset_seq;

CREATE TABLE IF NOT EXISTS quantity_detail (
    id bigint PRIMARY KEY,
    quantity_name varchar(255),
    unit varchar(255),
    is_obis boolean,
    medium varchar(10),
    channel varchar(10),
    measurement_variable varchar(10),
    measurement_type varchar(10),
    tariff varchar(10),
    previous_measurement varchar(10)
);

CREATE SEQUENCE IF NOT EXISTS quantity_detail_seq;

CREATE TABLE IF NOT EXISTS dataset_quantity_joins (
    device_data_set bigint NOT NULL references DATASET (id) ,
    quantity_detail bigint NOT NULL references QUANTITY_DETAIL (id),
    UNIQUE (device_data_set, quantity_detail)
);

CREATE TABLE IF NOT EXISTS implemented_table (
    id bigint PRIMARY KEY,
    table_name varchar(255),
    class_name varchar(255),
    creation_date_time timestamp,
    version integer,
    dataset_id bigint NOT NULL references DATASET (id)
);

CREATE SEQUENCE IF NOT EXISTS implemented_table_seq;

CREATE TABLE IF NOT EXISTS measurement (
    id bigint PRIMARY KEY,
    reference_device_id bigint,
    device_id varchar(32),
    dataset_id bigint references DATASET (id),
    source_filename varchar(255),
    source_file_id bigint,
    measurement_type varchar(255),
    measurement_type_by_time varchar(255) ,
    date_time_from timestamp,
    date_time_to timestamp ,
    records_type varchar(255),
    records_count integer,
    frequency_in_minutes integer
);


CREATE SEQUENCE IF NOT EXISTS measurement_seq;

CREATE TABLE IF NOT EXISTS measurement_header (
    measurement_id bigint NOT NULL references measurement (id),
    header_key varchar(255) NOT NULL,
    header_value varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS records (
    record_value double precision NOT NULL,
    date_time_from timestamp NOT NULL,
    date_time_to timestamp NOT NULL,
    dataset_id bigint NOT NULL references DATASET (id),
    quantity_details_id bigint NOT NULL references QUANTITY_DETAIL (id)
) PARTITION BY LIST (dataset_id);

CREATE TABLE records_3046188 PARTITION OF records FOR VALUES IN (110, 111, 112);
CREATE TABLE records_5725306 PARTITION OF records FOR VALUES IN (113,114, 115);
CREATE TABLE records_6416324 PARTITION OF records FOR VALUES IN (116, 117, 118);
CREATE TABLE records_8207547 PARTITION OF records FOR VALUES IN (119, 120, 121);
CREATE TABLE records_11111111 PARTITION OF records FOR VALUES IN (122);

CREATE INDEX IF NOT EXISTS record_time_from ON records(date_time_from);
CREATE INDEX IF NOT EXISTS record_time_to ON records(date_time_to);

