SELECT partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_relation_uu A
 WHERE A.serial_number_b=:SERIAL_NUMBER_B
   AND A.relation_type_code=:RELATION_TYPE_CODE
   AND sysdate < A.end_date
   AND A.START_DATE < A.end_date