UPDATE tf_f_blacksms
   SET end_date=to_date('20501231','yyyymmdd'),
       update_staff_id=:UPDATE_STAFF_ID,
       update_depart_id=:UPDATE_DEPART_ID,
       update_time=sysdate  
 WHERE serial_number=:SERIAL_NUMBER
   AND state=:STATE
   AND rsrv_str1=:RSRV_STR1
   AND rsrv_str2=:RSRV_STR2