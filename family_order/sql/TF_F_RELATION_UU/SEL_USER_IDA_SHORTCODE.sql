SELECT partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_relation_uu
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   --AND partition_id=MOD(USER_ID_B,10000)
   AND short_code=:SHORT_CODE 
   AND relation_type_code like '%'||:RELATION_TYPE_CODE||'%'
   AND SYSDATE BETWEEN start_date AND end_date