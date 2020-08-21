UPDATE tf_f_user_svc
   SET end_date = TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=sysdate  
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND service_id = :SERVICE_ID
   AND start_date+0 = TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')