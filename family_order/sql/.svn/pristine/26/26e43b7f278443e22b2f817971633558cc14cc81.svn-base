UPDATE tf_f_user_imei
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')-0.00001  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND start_date<end_date
   AND end_date>sysdate