UPDATE tf_f_user_product
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),
   	   UPDATE_TIME=sysdate,
   	   UPDATE_STAFF_ID=:UPDATE_STAFF_ID,
   	   UPDATE_DEPART_ID=:UPDATE_DEPART_ID
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND product_id = :PRODUCT_ID
   AND SYSDATE < end_date+0