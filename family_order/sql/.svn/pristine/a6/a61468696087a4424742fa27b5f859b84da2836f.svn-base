UPDATE tf_f_user_monitor
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   and end_date > sysdate