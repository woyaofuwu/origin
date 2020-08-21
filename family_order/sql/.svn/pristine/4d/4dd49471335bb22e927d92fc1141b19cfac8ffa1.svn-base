SELECT partition_id,to_char(user_id_a) user_id_a,to_char(user_id_b) user_id_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,serial_number_a,serial_number_b  
FROM tf_f_relation_uu   u
WHERE user_id_b = TO_NUMBER(:USER_ID_B)
AND sysdate < end_date+0
AND partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
and exists (select 1 from td_s_commpara t 
					where 	t.param_attr = 940
					and     u.relation_type_code  = t.para_code1
					
                )