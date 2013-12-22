

drop table cert;
drop table org; 
drop table org_role; 
drop table person; 
drop table topic; 
drop table topic_sub; 

drop table config;
drop table history; 
drop table entity_tran; 
drop table schema_version;

create table person (
  person_id serial, 
  email varchar(64),
  label varchar(64), 
  locale varchar(32),
  enabled boolean default false,
  inserted timestamp not null default now(),
  constraint uniq_person unique (email)
);

create table org (
  org_id serial, 
  org_domain varchar(64) not null,
  label varchar(64),
  enabled boolean default false,
  inserted timestamp not null default now(),
  constraint uniq_org unique (org_domain)
);

create table org_role (
  org_role_id serial, 
  org_domain varchar(64), 
  email varchar(64),
  role_type varchar(32),
  enabled boolean default false,
  inserted timestamp not null default now(),
  constraint uniq_org_role unique (org_domain, email)
);

create table cert (
  cert_id serial,
  org_domain varchar(64) not null,
  org_unit varchar(64) not null,
  common_name varchar(64) not null,
  encoded varchar(8192),
  enabled boolean default false,
  inserted timestamp not null default now(),
  constraint uniq_cert unique (org_domain, org_unit, common_name)
);

create table topic (
  topic_id serial,
  cert_id int,
  topic_label varchar(64), 
  enabled boolean default false,
  inserted timestamp not null default now(),
  constraint uniq_topic unique (cert_id, topic_label)
);

create table topic_sub (
  topic_sub_id serial, 
  topic_id int, 
  email varchar(64),
  enabled boolean default false,
  inserted timestamp not null default now(),
  constraint uniq_topic_sub unique (topic_id, email)
);

create table schema_version (
  version_number int,
  updated_time timestamp default now()
);

create table history (
  history_id serial,
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
  config_id serial,
  group_ varchar(64),
  key_ varchar(64),
  value_ varchar(128),
  constraint uniq_config unique (group_, key_)
);

create table entity_tran (
  app_id int,
  entity_tran_id serial,
  entity_id int,
  entity_key varchar(128),
  entity_data varchar(4096),
  entity_datum varchar(32),
  entity_value varchar(1024),
  time_ timestamp not null default now()
);

insert into schema_version (version_number) values (1);
