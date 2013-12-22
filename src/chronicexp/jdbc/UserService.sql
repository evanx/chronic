
-- insert
insert into user (
  email, label, enabled
) values (?, ?, ?)
;

-- update (enabled, user_id)
update user set enabled = ? where user_id = ?
;

-- update (label, user_id)
update user set label = ? where user_id = ?
;

-- select (email)
select * from user where email = ?
;

-- select (user_id)
select * from user where user_id = ?
;

-- delete (user_id)
delete from user where user_id = ?
;

-- list
select * from user order by email
;

-- list enabled
select * from user where enabled order by email
;
