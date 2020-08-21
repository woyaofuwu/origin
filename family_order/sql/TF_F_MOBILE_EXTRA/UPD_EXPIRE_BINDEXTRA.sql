UPDATE tf_f_mobile_extra
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),oper_staff_id=:OPER_STAFF_ID,oper_time=sysdate  
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND serial_number=:SERIAL_NUMBER
   AND bind_serial_number=:BIND_SERIAL_NUMBER
   AND sysdate BETWEEN start_date AND end_date