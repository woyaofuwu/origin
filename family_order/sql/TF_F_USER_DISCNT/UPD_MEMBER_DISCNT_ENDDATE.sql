UPDATE tf_f_user_discnt
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND discnt_code=:DISCNT_CODE
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')