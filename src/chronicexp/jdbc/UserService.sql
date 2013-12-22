
-- insert
insert into user (
  email, label, enabled
) values (?, ?, ?)
;

-- delete
delete from user where user_id = ?
;

-- update enabled
update user set enabled = ? where user_id = ?
;

-- update label
update user set label = ? where user_id = ?
;

-- select id
select * from user where user_id = ?
;

-- select key
select * from user where email = ?
;

-- list
select * from user order by email
;

-- list enabled
select * from user where enabled order by email
;
