UPDATE tf_f_customer
   SET cust_state=:CUST_STATE  
 WHERE partition_id=TO_NUMBER(:PARTITION_ID)
   AND cust_id=TO_NUMBER(:CUST_ID)