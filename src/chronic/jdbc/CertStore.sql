
-- insert
insert into cert (
  org_url,
  org_unit,
  common_name,
  encoded,
  enabled
) values (?, ?, ?, ?)
;

-- update encoded
update cert set encoded = ? where cert_id = ?
;

-- update address
update cert set address = ? where cert_id = ?
;

-- select key
select * from cert where org_url = ? and org_unit = ? and common_name = ?
;

-- select id
select * from cert where cert_id = ?
;

-- delete
delete from cert where cert_id = ?
;

-- list
select * from cert order by org_url, org_unit, common_name
;

-- list org
select * from cert where org_url = ? order by org_unit, common_name
;
