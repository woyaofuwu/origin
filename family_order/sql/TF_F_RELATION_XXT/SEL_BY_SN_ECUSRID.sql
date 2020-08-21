SELECT PARTITION_ID,
	USER_ID_A,
       SERIAL_NUMBER_A,
       SERIAL_NUMBER_B,
       ELEMENT_TYPE_CODE,
       ELEMENT_ID,
       RELA_INST_ID,
       INST_ID,
       to_char(START_DATE, 'YYYY-MM-DD hh24:mi:ss') START_DATE,
       to_char(END_DATE, 'YYYY-MM-DD hh24:mi:ss') END_DATE,
       NAME,
       EC_USER_ID,
       to_char(UPDATE_TIME, 'YYYY-MM-DD hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_STR6,
       RSRV_STR7,
       RSRV_STR8,
       RSRV_STR9,
       RSRV_STR10,
       RSRV_DATE1,
       RSRV_DATE2,
       RSRV_DATE3,
       SERVICE_ID
  FROM TF_F_RELATION_XXT
 WHERE 1=1
      AND SERIAL_NUMBER_A = :SERIAL_NUMBER_A 
      AND SYSDATE BETWEEN START_DATE AND END_DATE
