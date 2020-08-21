SELECT partition_id,
       to_char(user_id_a) user_id_a,
       to_char(user_id_b) user_id_b,
       relation_type_code,
       role_code_a,
       role_code_b,
       orderno,
       short_code,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM TF_F_RELATION_UU UU
 WHERE UU.RELATION_TYPE_CODE = '20'
   AND UU.USER_ID_B = TO_NUMBER(:USER_ID_B)
   AND UU.PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000)
   AND END_DATE + 0 > SYSDATE
   AND ROWNUM < 2