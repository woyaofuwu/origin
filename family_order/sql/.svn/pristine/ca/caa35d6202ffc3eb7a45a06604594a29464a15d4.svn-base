SELECT PARTITION_ID, to_char(USER_ID_A) USER_ID_A, SERIAL_NUMBER_A, to_char(USER_ID_B) USER_ID_B, SERIAL_NUMBER_B, RELATION_TYPE_CODE, ROLE_TYPE_CODE, ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, to_char(INST_ID) INST_ID, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3
FROM tf_f_relation_uu
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID_B),10000)
   AND user_id_b=TO_NUMBER(:USER_ID_B)
   AND relation_type_code=:RELATION_TYPE_CODE
   AND role_code_b='1' 
   AND sysdate-90 < start_date