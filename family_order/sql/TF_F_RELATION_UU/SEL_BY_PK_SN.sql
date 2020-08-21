SELECT partition_id,to_char(user_id_a) user_id_a,to_char(user_id_b) user_id_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_relation_uu
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND user_id_b=(SELECT user_id FROM tf_f_user WHERE serial_number=:SERIAL_NUMBER AND remove_tag='0')
   AND partition_id=mod(user_id_b,10000)
   AND relation_type_code=:RELATION_TYPE_CODE
   AND sysdate BETWEEN start_date AND end_date