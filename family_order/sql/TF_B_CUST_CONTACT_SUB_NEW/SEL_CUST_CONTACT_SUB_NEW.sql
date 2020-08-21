SELECT accept_month,to_char(cust_contact_id) cust_contact_id,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,sub_id_type_code,sub_id,service_type_code 
  FROM tf_b_cust_contact_sub_new
 WHERE accept_month=TO_NUMBER(substr(:CUST_CONTACT_ID,5,2))
   AND cust_contact_id=TO_NUMBER(:CUST_CONTACT_ID)