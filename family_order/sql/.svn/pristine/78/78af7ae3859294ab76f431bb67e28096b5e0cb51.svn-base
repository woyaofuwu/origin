SELECT partition_id,to_char(user_id) user_id,service_id,para_code,para_type_code,ip_address,rsrv_str1,rsrv_str2,rsrv_str3,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_user_apn
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND para_code=:PARA_CODE
   AND sysdate between start_date AND end_date