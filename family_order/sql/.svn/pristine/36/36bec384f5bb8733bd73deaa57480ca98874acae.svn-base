UPDATE tf_f_relation_uu
   SET orderno=:ORDERNO,short_code=:SHORT_CODE,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=sysdate
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND user_id_b=TO_NUMBER(:USER_ID_B)
   AND partition_id = MOD(TO_NUMBER(:USER_ID_B),10000)
   AND relation_type_code=:RELATION_TYPE_CODE
   AND role_code_b=:ROLE_CODE_B
   AND sysdate < end_date+0