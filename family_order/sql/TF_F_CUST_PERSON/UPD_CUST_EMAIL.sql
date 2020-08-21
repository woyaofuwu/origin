UPDATE tf_f_cust_person
   SET email =:EMAIL 
 WHERE cust_id=TO_NUMBER(:CUST_ID)
   AND partition_id = MOD(TO_NUMBER(:CUST_ID),10000)