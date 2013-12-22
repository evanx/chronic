
-- insert
insert into topic (
  cert_id, label, enabled
) values (?, ?, ?)
;

-- delete
delete from topic
where topic_id = ?
;

-- update (enabled, topic_id)
update topic_sub 
set enabled = ? 
where topic_id = ?
;

-- select
select * from sub 
where topic_id = ?
;

-- select email
select * 
from topic
;

-- list
select * 
from topic_sub 
;

-- list (topic_id)
select * 
from topic_sub
where topic_id = ? 
;

-- list (email)
select * 
from topic_sub
where email = ? 
;

