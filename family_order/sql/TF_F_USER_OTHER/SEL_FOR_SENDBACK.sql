SELECT partition_id,to_char(user_id) user_id,rsrv_value_code,rsrv_value,rsrv_tag1,
       to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id
  FROM tf_f_user_other
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND rsrv_str1 = :RSRV_STR1
   order by start_date