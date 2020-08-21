SELECT b.partition_id,to_char(b.user_id_a) user_id_a,b.serial_number_a,to_char(b.user_id_b) user_id_b,b.serial_number_b,b.relation_type_code,b.role_code_a,b.role_code_b,b.orderno,b.short_code,to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_relation_uu a,tf_f_relation_uu b
 WHERE a.user_id_a=b.user_id_a
and a.user_id_b=TO_NUMBER(:USER_ID_B)
and b.relation_type_code=:RELATION_TYPE_CODE
and a.role_code_b = '1'
and (:ROLE_CODE_B is null or b.role_code_b=:ROLE_CODE_B)
and b.end_date>sysdate
and a.end_date>sysdate