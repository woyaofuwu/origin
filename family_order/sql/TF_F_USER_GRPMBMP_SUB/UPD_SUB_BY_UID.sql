UPDATE tf_f_user_grpmbmp_sub

   SET end_date=sysdate

 WHERE user_id=:USER_ID 

   and partition_id=mod(:USER_ID,10000)

   and end_date>sysdate