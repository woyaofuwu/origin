UPDATE tf_f_user_svc
   SET serv_para1=:SERV_PARA1
 WHERE user_id = TO_NUMBER(:USER_ID)
  AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
  AND service_id = :SERVICE_ID
  AND sysdate BETWEEN start_date+0 AND end_date+0