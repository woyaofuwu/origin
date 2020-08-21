UPDATE tf_f_user_apn
SET end_date = To_Date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'), update_time = SYSDATE
WHERE user_id = to_number(:USER_ID)
 AND partition_id = MOD(to_number(:USER_ID), 10000)
 AND para_code = :PARA_CODE