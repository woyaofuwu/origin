SELECT 1 
       FROM tf_f_user_svc a
       WHERE a.User_Id=:USER_ID
       AND   a.service_id=:SERVICE_ID
       AND   months_between(trunc(SYSDATE,'mm'),trunc(a.end_date,'mm'))<:MONTH