UPDATE tf_f_user
   SET brand_code=:BRAND_CODE,product_id=:PRODUCT_ID,prepay_tag=:PREPAY_TAG  
 WHERE user_id=TO_NUMBER(:USER_ID) 
   and partition_id=mod(TO_NUMBER(:USER_ID),10000)