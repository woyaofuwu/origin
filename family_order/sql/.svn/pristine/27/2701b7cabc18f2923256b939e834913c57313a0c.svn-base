UPDATE tf_f_user_svc
  SET  package_id=TO_NUMBER(:PACKAGE_ID),product_id=TO_NUMBER(:PRODUCT_ID),update_time=sysdate
 WHERE user_id = TO_NUMBER(:USER_ID)    
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)  
   AND inst_id=TO_NUMBER(:INST_ID)
   AND SYSDATE < end_date+0