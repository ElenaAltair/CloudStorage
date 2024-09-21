--liquibase formatted sql

--changeset petrova:0004
create table if not exists public.authorities
(
    id serial primary key,
    username varchar(200) not null,
    authority varchar(200)
);

insert into public.authorities (username, authority)
values  ('Admin', 'ADMIN'),
        ('User', 'USER');


