
-- insert
insert into org_role (
  org_domain, email, role_type, enabled
) values (?, ?, ?, ?)
;

-- update (enabled, org_role_id)
update org_role set enabled = ? where org_role_id = ?
;

-- select (org_domain, email)
select * from org where org_domain = ? and email = ?
;

-- select (org_id, email)
select * from org where org_id = ? and email = ?
;

-- select (org_role_id)
select * from org where org_role_id = ?
;

-- delete (org_role_id)
delete from org where org_role_id = ?
;

-- list (org_domain)
select * from org where org_domain = ? order by email 
;

-- list (org_id)
select * from org where org_id = ? order by email 
;

