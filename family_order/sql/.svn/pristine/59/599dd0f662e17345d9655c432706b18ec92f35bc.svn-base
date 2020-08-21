UPDATE tf_f_relation_uu
   SET role_code_a=:ROLE_CODE_A,role_code_b=:ROLE_CODE_B,orderno=:ORDERNO,short_code=:SHORT_CODE  
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND user_id_b=TO_NUMBER(:USER_ID_B)
   AND partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   AND relation_type_code=:RELATION_TYPE_CODE
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')