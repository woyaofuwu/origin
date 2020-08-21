UPDATE tf_f_account_incre
   SET cust_id=TO_NUMBER(:NEW_CUST_ID) ,update_time=sysdate  
 WHERE cust_id=TO_NUMBER(:OLD_CUST_ID)