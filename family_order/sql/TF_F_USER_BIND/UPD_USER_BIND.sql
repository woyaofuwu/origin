UPDATE tf_f_user_bind
   SET rsrv_str1 = :RSRV_STR1, rsrv_str2 = :RSRV_STR2, rsrv_str3 = :RSRV_STR3, rsrv_str4 = :RSRV_STR4, rsrv_str5 = :RSRV_STR5, rsrv_str6 = :RSRV_STR6, rsrv_str7 = :RSRV_STR7, end_date = to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') 
 WHERE user_id = to_number(:USER_ID)
   AND bind_serial_number = :BIND_SERIAL_NUMBER