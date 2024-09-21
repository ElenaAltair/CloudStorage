--liquibase formatted sql

--changeset petrova:0002
create table if not exists public.users (
    id serial primary key not null,
    username varchar(200) not null UNIQUE,
    password varchar(200) not null,
    enabled varchar(200)
    )


--create table if not exists cloudstorage.users (
    --login varchar(200) not null UNIQUE,
    --password varchar(200) not null,
    --firstname varchar(200) not null,
    --lastName varchar(200) not null,
    --role varchar(200) not null,
    --PRIMARY KEY (login, password)
    --)
--rollback drop table netology.CUSTOMERS;