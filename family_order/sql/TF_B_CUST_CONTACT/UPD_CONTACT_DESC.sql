UPDATE tf_b_cust_contact
   SET contact_desc=contact_desc||CHR(13)||:CONTACT_DESC  
 WHERE accept_month=TO_NUMBER(substr(:CUST_CONTACT_ID,5,2))
   AND cust_contact_id=TO_NUMBER(:CUST_CONTACT_ID)