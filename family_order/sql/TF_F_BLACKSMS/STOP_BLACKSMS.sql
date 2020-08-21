UPDATE tf_f_blacksms
   SET end_date=sysdate,
       rsrv_str2=:RSRV_STR2,
       update_staff_id=:UPDATE_STAFF_ID,
       update_depart_id=:UPDATE_DEPART_ID,
       update_time=sysdate  
 WHERE serial_number=:SERIAL_NUMBER
   AND state=:STATE
   AND (rsrv_str1=:RSRV_STR1 OR :RSRV_STR1 IS NULL)
   AND sysdate BETWEEN start_date and end_date