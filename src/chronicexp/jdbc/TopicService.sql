
-- insert
insert into topic (
  cert_id, topic_label, enabled
) values (?, ?, ?)
returning topic_id
;

-- delete
delete from topic
where topic_id = ?
;

-- update enabled
update topic
set enabled = ? 
where topic_id = ?
;

-- select id
select * 
from topic
where topic_id = ?
;

-- select key
select * 
from topic
where cert_id = ?
and topic_label = ?
;

-- list
select * 
from topic 
;

-- list cert
select * 
from topic
where cert_id = ? 
;

-- list email
select * 
from topic
where email = ? 
;

