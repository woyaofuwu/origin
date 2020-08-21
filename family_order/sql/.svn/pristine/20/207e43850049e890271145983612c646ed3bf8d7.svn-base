SELECT partition_id,to_char(user_id) user_id,service_id,main_tag,state_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,update_time
  FROM tf_f_user_svcstate
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND service_id = :SERVICE_ID
   AND state_code IN ('1','7')
   AND end_date > SYSDATE