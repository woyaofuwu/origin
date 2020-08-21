UPDATE tf_f_user_svc a
   SET a.serv_para1 = to_char(to_number(a.SERV_PARA1) + :SERV_PARA1)
 WHERE a.user_id = :USER_ID
   AND a.service_id = :SERVICE_ID
   AND SYSDATE BETWEEN a.start_date AND a.end_date