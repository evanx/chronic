
-- insert
insert into person (
  email, label, enabled
) values (?, ?, ?)
;

-- delete
delete from person where email = ?
;

-- update enabled
update person set enabled = ? where email = ?
;

-- update label
update person set label = ? where email = ?
;

-- select key
select * from person where email = ?
;

-- list
select * from person order by email
;

-- list enabled
select * from person where enabled order by email
;
