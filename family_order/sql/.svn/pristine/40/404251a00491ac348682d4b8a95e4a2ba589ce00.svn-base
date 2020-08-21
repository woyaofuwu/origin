UPDATE tf_f_user_svc
   SET end_date = TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND service_id = :SERVICE_ID
   AND (serv_para8=:SERV_PARA8 OR :SERV_PARA8 IS NULL)
   AND end_date+0 > TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')