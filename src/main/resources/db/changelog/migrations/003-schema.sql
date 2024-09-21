--liquibase formatted sql

--changeset petrova:0003
insert into public.users (username, password, enabled)
values  ('Admin', '$2a$12$r7xMVd69qrcro2y08lCfcuGCcALKFwpM11Y8CTslU6excYVc24eE2', 1),
        ('User', '$2a$12$L/60e15uj8ZFqTldPHFJo.v3Tlyf4MXtTmPesF0rvWCRnrpiE5ggq', 1);





--insert into cloudstorage.users (login, password, firstname, lastname, role)
--values  ('User1', '$2a$12$L/60e15uj8ZFqTldPHFJo.v3Tlyf4MXtTmPesF0rvWCRnrpiE5ggq', 'Ivan', 'Smirnov', 'ROLE_USER'),
        --('User2', '$2a$12$L/60e15uj8ZFqTldPHFJo.v3Tlyf4MXtTmPesF0rvWCRnrpiE5ggq', 'Max', 'Petrov', 'ROLE_USER'),
        --('Admin', '$2a$12$r7xMVd69qrcro2y08lCfcuGCcALKFwpM11Y8CTslU6excYVc24eE2', 'Petr', 'Ivanov', 'ROLE_ADMIN');



-- login = User1; password = user1234;
-- login = User2; password = user1234;
-- login = Admin; password = admin;
