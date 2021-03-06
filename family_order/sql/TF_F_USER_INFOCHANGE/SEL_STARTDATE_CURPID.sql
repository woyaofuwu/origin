SELECT partition_id,to_char(user_id) user_id,product_id,brand_code,serial_number,imsi,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_infochange
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)  
   AND product_id IN (SELECT product_id FROM tf_f_user WHERE user_id=TO_NUMBER(:USER_ID) AND  remove_tag='0')
 ORDER BY start_date