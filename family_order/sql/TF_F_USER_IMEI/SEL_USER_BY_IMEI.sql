SELECT to_char(user_id) user_id,serial_number,imei,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_imei
 WHERE imei=:IMEI
 And end_date >Sysdate