UPDATE tf_f_user_svc
   SET end_date = to_date(:FINISH_DATE,'yyyy-mm-dd hh24:mi:ss'),update_time= to_date(:FINISH_DATE,'yyyy-mm-dd hh24:mi:ss')
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date > SYSDATE