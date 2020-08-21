UPDATE tf_f_user_mbmp
   SET end_date=sysdate 
   WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND serial_number=:SERIAL_NUMBER
   AND biz_type_code=:BIZ_TYPE_CODE
   AND sysdate BETWEEN start_date AND end_date