UPDATE tf_f_user_grpmbmp_info
   SET end_date=TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')
 WHERE user_id=:USER_ID 
   and partition_id=mod(:USER_ID,10000)
   and end_date>sysdate