
-- insert
insert into topic_sub (
  org_url,
  org_unit,
  common_name,
  encoded,
  enabled
) values (?, ?, ?, ?)
;

-- update encoded
update topic_sub set encoded = ? where topic_sub_id = ?
;

-- update address
update topic_sub set address = ? where topic_sub_id = ?
;

-- select key
select * from topic_sub where org_url = ? and org_unit = ? and common_name = ?
;

-- select id
select * from topic_sub where topic_sub_id = ?
;

-- delete
delete from topic_sub where topic_sub_id = ?
;

-- list
select * from topic_sub order by org_url, org_unit, common_name
;

-- list org
select * from topic_sub where org_url = ? order by org_unit, common_name
;
