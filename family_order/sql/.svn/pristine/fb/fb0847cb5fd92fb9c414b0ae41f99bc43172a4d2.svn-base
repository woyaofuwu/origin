UPDATE tf_f_user_imei
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND imei=:IMEI
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')