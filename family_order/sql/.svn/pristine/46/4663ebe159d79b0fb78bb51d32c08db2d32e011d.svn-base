SELECT staff_id,to_char(cust_id) cust_id,to_char(cust_contact_id) cust_contact_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(dead_line,'yyyy-mm-dd hh24:mi:ss') dead_line 
  FROM tl_b_recent_cust_contact
 WHERE staff_id=rpad(:STAFF_ID,8,' ')