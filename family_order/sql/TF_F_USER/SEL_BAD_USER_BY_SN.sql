SELECT partition_id,to_char(user_id) user_id,brand_code,product_id,serial_number,to_char(in_date,'yyyy-mm-dd hh24:mi:ss') in_date,to_char(open_date,'yyyy-mm-dd hh24:mi:ss') open_date,to_char(first_call_time,'yyyy-mm-dd hh24:mi:ss') first_call_time,open_mode,develop_depart_id,develop_staff_id 
  FROM tf_f_user
 WHERE serial_number=:SERIAL_NUMBER
   AND remove_tag = '0'