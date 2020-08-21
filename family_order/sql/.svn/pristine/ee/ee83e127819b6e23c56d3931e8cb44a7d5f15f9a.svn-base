SELECT partition_id,to_char(user_id) user_id,serial_number,sp_id,sp_name,biz_code,biz_desc,to_char(book_date,'yyyy-mm-dd hh24:mi:ss') book_date,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,contract_id,price,billing_type,staff_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,subscribe_id,to_char(expect_time,'yyyy-mm-dd hh24:mi:ss') expect_time,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10 
  FROM tf_f_user_sign
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND serial_number=:SERIAL_NUMBER
   and end_date > sysdate