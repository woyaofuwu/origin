UPDATE tf_f_bankband
   SET end_time=TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND SYSDATE BETWEEN start_time AND end_time