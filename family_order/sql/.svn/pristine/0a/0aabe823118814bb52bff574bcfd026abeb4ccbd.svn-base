UPDATE tf_f_user_reuse_tj
   SET tag=:TAG_NEW,occupy_time=SYSDATE,occupy_depart_id=:OCCUPY_DEPART_ID,rsrv_str1=:RSRV_STR1   
 WHERE serial_number=:SERIAL_NUMBER
   AND tag=:TAG_OLD
   AND occupy_staff_id=:OCCUPY_STAFF_ID