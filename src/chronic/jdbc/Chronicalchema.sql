
--drop table service_cert; 

drop table cert;
drop table config;
drop table contact_group;
drop table contact_group_member;
drop table contact;
drop table history; 
drop table host_;
drop table metric_record;
drop table org; 
drop table service_record;
drop table service_type;
drop table service;
drop table schema_revision;
drop table user_; 

create table schema_revision (
  revision_number int,
  updated timestamp default now()
);

create table cert (
  cert_id int auto_increment primary key,
  org_id int not null,
  name_ varchar(64) not null,
  subject varchar(255) not null,
  cert varchar(8192),
  inserted timestamp not null default now(),
  updated timestamp not null default now(),
  unique key uniq_cert (name_)
);

create table config (
  config_id int auto_increment primary key,
  name_ varchar(128),
  value_ varchar(128)
);

create table history (
  history_id int auto_increment primary key,
  entity_id int not null,
  table_ varchar(32) not null,
  column_ varchar(32),
  value_ varchar(32),
  value_type varchar(32),
  comment_ varchar,
  inserted timestamp not null default now(),
  updated timestamp not null default now(),
  updated_by varchar(32) not null
);

create table org (
  org_id int auto_increment primary key, 
  org_name varchar(32) not null,
  display_name varchar(48),
  url varchar(32),
  enabled boolean default true,
  inserted timestamp not null default now(),
  updated timestamp not null default now(),
  unique key uniq_org_name (org_name)
);

create table user_ (
  user_id int auto_increment primary key, 
  user_name varchar(32) not null, 
  first_name varchar(32), 
  last_name varchar(32), 
  display_name varchar(64), 
  email varchar(64),
  role_ varchar(32),
  subject varchar(255),
  secret varchar(16),
  cert varchar(8192),
  password_hash varchar(64),
  password_salt varchar(32),
  challenge varchar(64),
  answer varchar(64),
  login timestamp,
  logout timestamp,
  country varchar(2),
  language_ varchar(2),
  locale varchar(32),
  enabled boolean default true,
  inserted timestamp not null default now(),
  updated timestamp not null default now(),
  unique key uniq_user_name (user_name)
);

create table contact (
  org_id int not null,
  contact_id int auto_increment primary key, 
  contact_name varchar(128),
  enabled boolean default true,
  inserted timestamp not null default now(),
  updated timestamp not null default now(),
  unique key uniq_contact_name (contact_name)
);

create table contact_group (
  org_id int not null,
  contact_group_id int auto_increment primary key, 
  contact_group_name varchar(32),
  enabled boolean default true,
  inserted timestamp not null default now(),
  updated timestamp not null default now(),
  unique key uniq_contact_group (org_id, contact_group_name)
);

create table contact_group_member (
  contact_group_membership_id int auto_increment primary key,
  contact_group_id int,
  contact_id int, 
  ordinal int,
  enabled boolean default true,
  inserted timestamp default now(),
  updated timestamp default now(),
  unique key unique_contact_group_member (contact_group_id, contact_id)
);

create table host_ (
  org_id int not null,
  host_id int auto_increment primary key, 
  host_name varchar(32) not null,
  cert varchar(8192),
  enabled boolean default true,
  inserted timestamp not null default now(),
  updated timestamp not null default now(),
  unique key uniq_host (org_id, host_name)
);

create table service_type (
  org_id int not null,
  service_id int auto_increment primary key, 
  service_name varchar(32) not null,
  enabled boolean default true,
  inserted timestamp not null default now(),
  updated timestamp not null default now(),
  unique key uniq_service_type (org_id, service_name)
);

create table service (
  org_id int not null,
  service_id int auto_increment primary key, 
  host_name varchar(32),
  service_name varchar(32),
  subject varchar(255),
  cert varchar(8192),
  inserted timestamp not null default now(),
  updated timestamp not null default now(),
  unique key uniq_service (org_id, host_name, service_name)
);

create table service_record (
  org_id int not null,
  service_record_id int auto_increment primary key, 
  cert_name varchar(64),
  service_name varchar(32),
  status varchar(16),
  time_ timestamp,
  dispatched_time timestamp,
  notified_time timestamp,
  exit_code integer,
  out_ varchar,
  err_ varchar,
  notify boolean,
  unique key uniq_service_record (org_id, cert_name, service_name, time_)
);

create table metric_record (
  org_id int not null,
  metric_id int auto_increment primary key, 
  metric_name varchar(32),
  host_name varchar(32),
  service_name varchar(32),
  value_ float,
  time_ timestamp,
  unique key uniq_metric_record (org_id, host_name, service_name, metric_name, time_)
);


alter table cert add constraint fk_cert_org_id foreign key (org_id) references org (org_id);
