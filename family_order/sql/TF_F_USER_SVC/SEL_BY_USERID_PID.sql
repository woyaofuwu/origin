SELECT partition_id,to_char(user_id) user_id,service_id,main_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8 
  FROM tf_f_user_svc a
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND EXISTS(SELECT 1 FROM td_b_product_svc WHERE product_id=:PRODUCT_ID AND service_id=a.service_id AND sysdate BETWEEN start_date AND end_date)
   AND end_date>sysdate