SELECT partition_id,to_char(user_id) user_id,service_id,main_tag,state_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_user_svcstate
 WHERE partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND main_tag='1'
   AND sysdate+0.00001 between start_date and end_date