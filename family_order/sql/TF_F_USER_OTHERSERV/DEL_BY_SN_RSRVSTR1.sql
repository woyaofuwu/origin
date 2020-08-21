UPDATE tf_f_user_otherserv
 SET end_date = sysdate, rsrv_date1 = sysdate
 WHERE service_mode=:SERVICE_MODE
   AND serial_number=:SERIAL_NUMBER
   AND rsrv_str1=:RSRV_STR1
   AND start_date <= sysdate AND end_date > sysdate