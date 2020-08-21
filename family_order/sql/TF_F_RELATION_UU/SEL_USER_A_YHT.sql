SELECT partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(inst_id) inst_id
, RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
  FROM tf_f_relation_uu
 WHERE user_id_a = TO_NUMBER(:USER_ID_A)
    AND relation_type_code = :RELATION_TYPE_CODE
    AND end_date > start_date
    AND end_date > sysdate
    ORDER BY orderno