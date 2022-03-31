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
    drop constraint if exists UK_mkjheedol1oe4evwyjw7ixpot;
alter table if exists usr
    add constraint UK_mkjheedol1oe4evwyjw7ixpot unique (name);
alter table if exists find_sex
    add constraint FKtmarx5m5pava4m1f7594ubht7 foreign key (user_id) references usr;
alter table if exists user_relationships
    add constraint FKe1faoi6vindin0uh9oukjikdi foreign key (like_id) references usr;
alter table if exists user_relationships
    add constraint FKaiy05gpdwu1pemrw6il5xr1wr foreign key (user_id) references usr;
alter table if exists user_role
    add constraint FKfpm8swft53ulq2hl11yplpr5 foreign key (user_id) references usr;