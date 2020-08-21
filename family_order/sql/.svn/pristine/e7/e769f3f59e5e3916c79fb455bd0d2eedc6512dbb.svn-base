UPDATE tf_b_cust_contact
   SET finish_time=TO_DATE(:FINISH_TIME, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE accept_month=to_number(substr(:CUST_CONTACT_ID,5,2))
   AND cust_contact_id=TO_NUMBER(:CUST_CONTACT_ID)