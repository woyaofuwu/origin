SELECT  *
from tf_f_user_svc a
where user_id=to_number(:USER_ID)
 and partition_id=mod(to_number(:USER_ID),10000)
 and service_id in ('15','19')
 and sysdate BETWEEN start_date AND end_date