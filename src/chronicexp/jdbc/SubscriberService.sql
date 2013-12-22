
-- insert
insert into topic_sub (
  topic_id,
  email,
  enabled
) values (?, ?, ?)
returning topic_sub_id
;

-- delete
delete from topic_sub 
where topic_sub_id = ?
;

-- update enabled
update topic_sub 
set enabled = ? 
where topic_sub_id = ?
;

-- select id
select * from topic_sub 
where topic_sub_id = ?
;

-- select key
select * 
from topic_sub 
where topic_id = ?
and email = ?
;

-- list
select * 
from topic_sub 
;

-- list topic
select * 
from topic_sub
where topic_id = ? 
;

-- list email
select * 
from topic_sub
where email = ? 
;

