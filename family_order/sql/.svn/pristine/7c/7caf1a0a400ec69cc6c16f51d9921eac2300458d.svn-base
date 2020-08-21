SELECT COUNT(1) recordcount 
       FROM tf_f_user_discnt a
       WHERE a.User_Id=:USER_ID
       AND   a.discnt_code+0=:DISCNT_CODE
       AND   months_between(trunc(SYSDATE,'mm'),trunc(a.end_date,'mm'))<3