
-- insert
insert into cert (
  org_url,
  org_unit,
  cn,
  encoded,
  enabled
) values (?, ?, ?, ?)
;

-- delete
delete from cert where cert_id = ?
;

-- list
select * from cert order by org_url
;

-- update address
update cert
set address = ?
where org_url = ?
;

-- enabled
select count(1) from cert where org_url = ? and enabled
;

-- find_id
select * from cert where cert_id = ?
;

