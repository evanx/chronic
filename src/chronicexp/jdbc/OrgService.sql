
-- insert
insert into org (
  org_domain, label, enabled
) values (?, ?, ?)
;

-- delete
delete from org where org_domain = ?
;

-- update enabled
update org set enabled = ? where org_domain = ?
;

-- update label
update org set label = ? where org_domain = ?
;

-- select id
select * from org where org_domain = ?
;

-- select key
select * from org where org_domain = ?
;

-- list
select * from org order by org_domain
;

