DELETE FROM tf_f_user_brandchange
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = mod(:USER_ID,10000)
   AND start_date >= end_date