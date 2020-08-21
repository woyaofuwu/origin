SELECT t.serial_number_a,t.serial_number_b,t.user_id_a,t.user_id_b,t.relation_type_code,t1.relation_type_name,t.role_code_a,t.role_code_b,t.short_code,t.start_date,t.end_date 
FROM tf_f_relation_uu t,td_s_relation t1
WHERE t.serial_number_a=:SERIAL_NUMBER_A
AND t.serial_number_b=:SERIAL_NUMBER_B
AND t.relation_type_code=:RELATION_TYPE_CODE
AND t.relation_type_code=t1.relation_type_code