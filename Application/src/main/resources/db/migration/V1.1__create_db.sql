create schema if not exists jtinder_db;

create sequence if not exists hibernate_sequence;

create table find_sex
(
    user_id  int8 not null,
    find_sex varchar(255)
);

create table user_relationships
(
    user_id int8 not null,
    like_id int8 not null,
    primary key (like_id, user_id)
);
create table user_role
(
    user_id int8 not null,
    roles   varchar(255)
);
create table usr
(
    user_id     int8 not null,
    description varchar(255),
    name        varchar(255),
    password    varchar(255),
    sex         varchar(255),
    primary key (user_id)
);
alter table if exists usr
    drop constraint if exists USR_UNIQUE_NAME;
alter table if exists find_sex
    add constraint FKtmarx5m5pava4m1f7594ubht7 foreign key (user_id) references usr;
alter table if exists user_relationships
    add constraint FK_LIKE_ID_REF_USR foreign key (like_id) references usr;
alter table if exists user_relationships
    add constraint FK_USER_ID_REF_USR_REL foreign key (user_id) references usr;
alter table if exists user_role
    add constraint FK_USER_ID_REF_USER_ROLE  foreign key (user_id) references usr;