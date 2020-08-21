SELECT to_char(cust_contact_id) cust_contact_id,accept_month,to_char(trade_id) trade_id,to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,trace_type_code,trace_route 
  FROM tf_b_cust_contact_trace
 WHERE cust_contact_id=TO_NUMBER(:CUST_CONTACT_ID)
   AND accept_month=:ACCEPT_MONTH