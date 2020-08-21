SELECT partition_id,
       to_char(user_id_a) user_id_a,
       serial_number_a,
       to_char(user_id_b) user_id_b,
       serial_number_b,
       relation_type_code,
       role_code_a,
       role_code_b,
       orderno,
       short_code,	
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM TF_F_RELATION_UU T
 WHERE T.USER_ID_A = TO_NUMBER(:USER_ID_A)
   AND T.RSRV_STR4 = :ACTIVE_TYPE
   AND T.RELATION_TYPE_CODE = 'SA'
   AND T.RSRV_TAG1 = '1'
   AND T.END_DATE > SYSDATE