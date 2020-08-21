SELECT to_char(user_id) user_id,to_char(user_id_b) user_id_b,serial_number_b,short_code,serial_number,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_str1,rsrv_str2,rsrv_str3 
  FROM tf_f_vpmn_memberout
 WHERE serial_number = :SERIAL_NUMBER
   AND sysdate between start_date and end_date