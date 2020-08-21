select COUNT(1) recordcount
from tf_f_relation_uu a
where partition_id=mod(to_number(:USER_ID),10000)
  and user_id_b=to_number(:USER_ID)
  and sysdate between start_date and end_date
  and exists (select 1 from tf_f_user
			   where serial_number in ('V0SJ004999','V0SJ004888','V0HN001010')
			    and remove_tag='0'
			    and user_id=a.user_id_a)