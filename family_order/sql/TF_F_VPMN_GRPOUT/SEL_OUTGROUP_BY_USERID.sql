SELECT to_char(user_id) user_id,out_group_id,serial_number,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_str1,rsrv_str2,rsrv_str3 
  FROM tf_f_vpmn_grpout
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND sysdate between  start_date and end_date