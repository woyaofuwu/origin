SELECT 1 
       FROM tf_f_user_platsvc a
       WHERE a.User_Id=:USER_ID
       AND   a.sp_code=:SP_CODE
       AND   months_between(trunc(SYSDATE,'mm'),trunc(a.end_date,'mm'))<:MONTH