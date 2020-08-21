DELETE FROM TF_F_USER_SALE_ACTIVE
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND start_date > end_date