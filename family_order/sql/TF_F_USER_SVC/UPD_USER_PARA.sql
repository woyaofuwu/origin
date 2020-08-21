UPDATE tf_f_user_svc
   SET serv_para1=:SERV_PARA1,serv_para2=:SERV_PARA2,serv_para3=:SERV_PARA3,serv_para4=:SERV_PARA4,serv_para5=:SERV_PARA5,serv_para6=:SERV_PARA6,serv_para7=:SERV_PARA7,serv_para8=:SERV_PARA8  
 WHERE user_id = TO_NUMBER(:USER_ID)
  AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
  AND service_id = :SERVICE_ID
  AND sysdate BETWEEN start_date+0 AND end_date+0