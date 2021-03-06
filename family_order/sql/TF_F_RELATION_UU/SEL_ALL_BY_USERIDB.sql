SELECT partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,inst_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_relation_uu
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID_B),10000)
   AND user_id_b=TO_NUMBER(:USER_ID_B)
   AND TRUNC(LAST_DAY(SYSDATE ) + 1) between start_date and end_date