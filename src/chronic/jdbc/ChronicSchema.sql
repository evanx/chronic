

drop table cert;
drop table org; 
drop table org_role; 
drop table person; 
drop table topic; 
drop table topic_sub; 

drop table config;
drop table history; 
drop table schema_revision;

create table person (
  person_id int auto_increment primary key, 
  email varchar(64),
  label varchar(64), 
  locale varchar(32),
  enabled boolean default false,
  inserted timestamp not null default now(),
  unique key uniq_user (email)
);

create table org (
  org_id int auto_increment primary key, 
  org_domain varchar(64) not null,
  label varchar(64),
  enabled boolean default false,
  inserted timestamp not null default now(),
  unique key uniq_org_name (org_domain)
);

create table org_role (
  role_id int auto_increment primary key, 
  org_domain varchar(64), 
  email varchar(64),
  role_type varchar(32),
  enabled boolean default false,
  inserted timestamp not null default now(),
  unique key uniq_role (email)
);

create table cert (
  cert_id int auto_increment primary key,
  org_domain varchar(64) not null,
  org_unit varchar(64) not null,
  common_name varchar(64) not null,
  encoded varchar(8192),
  enabled boolean default false,
  inserted timestamp not null default now(),
  unique key uniq_cert (org_domain, org_unit, common_name)
);

create table topic (
  topic_id int auto_increment primary key,
  cert_id int,
  label varchar(64), 
  enabled boolean default false,
  inserted timestamp not null default now(),
  unique key uniq_cert (org_domain, org_unit, common_name, label)
);

create table topic_sub (
  sub_id int auto_increment primary key, 
  topic_id int, 
  email varchar(64),
  sub_type varchar(32),
  enabled boolean default false,
  inserted timestamp not null default now(),
  unique key uniq_sub (email)
);

create table schema_revision (
  revision_number int,
  time_ timestamp default now()
);

create table history (
  history_id int auto_increment primary key,
  entity_id int not null,
  table_ varchar(32) not null,
  column_ varchar(32),
  value_ varchar(32),
  value_type varchar(32),
  comment_ varchar,
  time_ timestamp not null default now(),
  user_ varchar(32) not null
);

create table config (
  config_id int auto_increment primary key,
  group_ varchar(64),
  key_ varchar(64),
  value_ varchar(128),
  unique key uniq_config (group_, key_)
);

