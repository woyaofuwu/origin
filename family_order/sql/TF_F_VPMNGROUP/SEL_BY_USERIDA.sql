SELECT to_char(user_id_a) user_id_a,to_char(user_id_b) user_id_b,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 
  FROM tf_f_vpmngroup
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
 AND end_date>sysdate