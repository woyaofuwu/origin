SELECT partition_id,to_char(user_id_a) user_id_a,to_char(user_id_b) user_id_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,serial_number_a,serial_number_b
  FROM tf_f_relation_uu
WHERE user_id_b = TO_NUMBER(:USER_ID_B)
   AND (relation_type_code IN (SELECT relation_type_code FROM td_s_relation WHERE relation_kind = 'F') OR (relation_type_code='34'))
   AND role_code_b = :ROLE_CODE_B
   AND sysdate < end_date+0
   AND partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)