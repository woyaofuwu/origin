SELECT COUNT(1) recordcount
  FROM tf_f_user
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND product_id = :PRODUCT_ID