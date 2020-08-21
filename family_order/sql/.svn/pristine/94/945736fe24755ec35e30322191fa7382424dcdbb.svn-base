UPDATE tf_f_cust_person
   SET sex=:SEX,post_code=:POST_CODE,post_address=:POST_ADDRESS,contact=:CONTACT  
 WHERE cust_id=TO_NUMBER(:CUST_ID)
   AND partition_id = MOD(TO_NUMBER(:CUST_ID),10000)