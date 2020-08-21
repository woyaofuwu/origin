SELECT partition_id,to_char(user_id) user_id,rsrv_value_code,rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_other
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   and (rsrv_str1,rsrv_str2,start_date) in
   (select rsrv_str1,rsrv_str2,max(start_date) start_date 
   from TF_F_USER_OTHER 
   WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   and user_id = TO_NUMBER(:USER_ID)
   and RSRV_VALUE_CODE = :RSRV_VALUE_CODE
   and end_date > sysdate
   group by rsrv_str1,rsrv_str2)
   and end_date > sysdate