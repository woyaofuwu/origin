SELECT partition_id,to_char(user_id) user_id,service_id,main_tag,state_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM tf_f_user_svcstate
 WHERE user_id = :USER_ID
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND main_tag = '1'
   AND state_code =:STATECODE
   AND end_date > sysdate