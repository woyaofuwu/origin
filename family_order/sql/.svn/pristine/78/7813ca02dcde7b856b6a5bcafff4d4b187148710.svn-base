UPDATE tf_f_user_sign
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND serial_number=:SERIAL_NUMBER
   AND biz_code=:BIZ_CODE
   and end_date > sysdate