SELECT distinct(product_id) PRODUCT_ID
  FROM tf_f_user_grp_package t
 where 1=1
     and t.user_id = :USER_ID    
     and SYSDATE BETWEEN t.START_DATE and t.END_DATE
     and partition_id=mod(TO_NUMBER(:USER_ID),10000)