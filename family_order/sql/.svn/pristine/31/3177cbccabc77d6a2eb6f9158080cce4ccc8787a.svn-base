SELECT COUNT(1) recordcount
  FROM tf_b_cust_contact
 WHERE staff_id=:STAFF_ID
   AND START_TIME BETWEEN to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') AND to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')