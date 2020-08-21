UPDATE tf_f_customer
   SET cust_name=:CUST_NAME,
       develop_staff_id = :DEVELOP_STAFF_ID,
       IN_STAFF_ID = :IN_STAFF_ID
 WHERE cust_id=TO_NUMBER(:CUST_ID) 
 AND PARTITION_ID=MOD(TO_NUMBER(:CUST_ID),10000)