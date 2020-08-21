UPDATE tf_b_cust_contact_new
   SET contact_desc=:CONTACT_DESC  
 WHERE accept_month=TO_NUMBER(substr(:CUST_CONTACT_ID,5,2))
   AND cust_contact_id=TO_NUMBER(:CUST_CONTACT_ID)