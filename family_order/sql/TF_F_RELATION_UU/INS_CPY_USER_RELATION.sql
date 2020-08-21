INSERT INTO tf_f_relation_uu(partition_id,user_id_a,serial_number_a,user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,start_date,end_date,update_time)
 SELECT MOD(TO_NUMBER(:USER_ID_B),10000),user_id_a,serial_number_a,TO_NUMBER(:USER_ID_B),serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,start_date,end_date,sysdate
   FROM tf_f_relation_uu 
  WHERE partition_id = TO_NUMBER(:PARTITION_ID)
    AND user_id_b = :USER_ID_A
    AND sysdate < end_date