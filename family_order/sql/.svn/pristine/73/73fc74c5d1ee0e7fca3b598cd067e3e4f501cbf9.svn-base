SELECT to_char(user_id_b) user_id_b,to_char(user_id_a) user_id_a,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_relation_blacklist
 WHERE user_id_b=TO_NUMBER(:USER_ID_B)
   AND user_id_a=TO_NUMBER(:USER_ID_A)
   AND (relation_type_code=:RELATION_TYPE_CODE OR relation_type_code='ZZ')
   AND start_date<=SYSDATE 
   AND end_date>=SYSDATE