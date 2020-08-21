SELECT to_char(cust_contact_id) cust_contact_id,accept_month,sub_id_type,sub_id,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,serial_number 
  FROM tf_b_cust_contact_sub
 WHERE cust_contact_id=TO_NUMBER(:CUST_CONTACT_ID) AND accept_month=TO_NUMBER(SUBSTR(:CUST_CONTACT_ID,5,2))