UPDATE tf_f_user_svcstate
   SET end_date = TO_DATE(:END_DATE,
                          'YYYY-MM-DD HH24:MI:SS') 
 WHERE user_id = :USER_ID
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000) 
   AND service_id = :SERVICE_ID 
   AND SYSDATE BETWEEN start_date AND end_date