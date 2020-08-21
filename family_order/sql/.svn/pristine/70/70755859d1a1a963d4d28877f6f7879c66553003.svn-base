SELECT serial_number,state,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_str1,rsrv_str2,rsrv_str3,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_o_trashcalluser
 WHERE (serial_number = :SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
   AND state=:STATE
   AND SYSDATE BETWEEN start_date and end_date