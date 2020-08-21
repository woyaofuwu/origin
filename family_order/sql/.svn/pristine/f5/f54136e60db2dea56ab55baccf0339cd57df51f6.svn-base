SELECT partition_id,to_char(user_id) user_id,rsrv_value_code,rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_other
 WHERE rsrv_value_code=:RSRV_VALUE_CODE
   AND rsrv_str5=:RSRV_STR5
   AND sysdate between start_date and end_date