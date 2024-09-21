--liquibase formatted sql

--changeset petrova:0005
create table if not exists public.files (
    id serial primary key,
    name varchar(200),
    content_type varchar(200),
    size bigint,
    data timestamp,
    login varchar(200),
    content bytea,
    FOREIGN KEY (login) REFERENCES public.users (username)
    );

insert into public.files(name, content_type, size, data, login)
    values  ('1_2024-09-20T15:10:54.808426900Z.txt','text/plain',5,'2024-09-20T15:10:54.808426900Z','Admin'),
            ('2_2024-09-20T15:17:47.731116200Z.txt','text/plain',5,'2024-09-20T15:17:47.731116200Z','Admin'),
            ('3_2024-10-20T15:17:47.731116200Z.txt','text/plain',5,'2024-10-20T15:17:47.731116200Z','Admin');
--rollback drop table netology.CUSTOMERS;