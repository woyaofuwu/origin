select user_id,to_char(end_date+1,'yyyy-MM-DD') || ' 00:00:00' new_start_date 
from tf_F_user_discnt 
where 
partition_id = mod(to_number(:USER_ID),10000)
and user_id = :USER_ID