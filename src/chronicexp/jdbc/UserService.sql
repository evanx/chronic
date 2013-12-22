
-- insert
insert into person (
  email, label, enabled
) values (?, ?, ?)
returning person_id
;

-- delete
delete from person where person_id = ?
;

-- update enabled
update person set enabled = ? where person_id = ?
;

-- update label
update person set label = ? where person_id = ?
;

-- select id
select * from person where person_id = ?
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
