
-- insert
insert into org_role (
  org_domain, email, role_type, enabled
) values (?, ?, ?, ?)
returning org_role_id
;

-- delete
delete from org_role where org_role_id = ?
;

-- update enabled
update org_role set enabled = ? where org_role_id = ?
;

-- select id
select * from org_role where org_role_id = ?
;

-- select key
select * from org_role where org_domain = ? and email = ?
;

-- list org
select * from org_role where org_domain = ? order by email 
;

-- list email
select * from org_role where email = ? 
;
