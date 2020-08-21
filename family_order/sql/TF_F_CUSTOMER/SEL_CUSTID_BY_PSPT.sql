SELECT to_char(cust_id) cust_id ,
cust_name, 
pspt_type_code
  FROM tf_f_customer
 WHERE  pspt_id=:PSPT_ID