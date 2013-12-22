
-- insert
insert into cert (
  org_domain,
  org_unit,
  common_name,
  encoded,
  enabled
) values (?, ?, ?, ?, ?)
returning cert_id
;

-- update enabled
update cert set enabled = ? where cert_id = ?
;

-- update encoded
update cert set enabled = ?, encoded = ? where cert_id = ?
;

-- update address
update cert set address = ? where cert_id = ?
;

-- select key
select * from cert where org_domain = ? and org_unit = ? and common_name = ?
;

-- select id
select * from cert where cert_id = ?
;

-- delete
delete from cert where cert_id = ?
;

-- list
select * from cert order by org_domain, org_unit, common_name
;

-- list org
select * from cert where org_domain = ? order by org_unit, common_name
;
