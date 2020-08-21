SELECT a.partition_id,to_char(a.user_id) user_id,b.brand_code,b.product_id,a.serial_number,to_char(a.in_date,'yyyy-mm-dd hh24:mi:ss') in_date,to_char(a.open_date,'yyyy-mm-dd hh24:mi:ss') open_date,to_char(a.first_call_time,'yyyy-mm-dd hh24:mi:ss') first_call_time,a.open_mode,a.develop_depart_id,a.develop_staff_id 
  FROM tf_f_user a,tf_f_user_product b
 WHERE a.user_id= b.user_id and a.serial_number=:SERIAL_NUMBER
   AND a.remove_tag = '0'
   and b.main_tag = '1' and sysdate between b.start_date and b.end_date