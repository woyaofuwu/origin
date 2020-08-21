SELECT serial_number,state,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_str1,rsrv_str2,rsrv_str3,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_blackcall
 WHERE serial_number=:SERIAL_NUMBER
   AND state=:STATE
   AND (rsrv_str1=:RSRV_STR1 or :RSRV_STR1 IS NULL)
   AND sysdate BETWEEN start_date and end_date