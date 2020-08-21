SELECT a.PARTITION_ID,
       a.USER_ID_A AS USER_ID,
       a.SERIAL_NUMBER_A,
       a.SERIAL_NUMBER_B,
       a.ELEMENT_TYPE_CODE,
       a.ELEMENT_ID,
       a.RELA_INST_ID,
       a.INST_ID,
       to_char(a.START_DATE, 'YYYY-MM-DD hh24:mi:ss') START_DATE,
       to_char(a.END_DATE, 'YYYY-MM-DD hh24:mi:ss') END_DATE,
       a.NAME,
       a.EC_USER_ID AS USER_ID_A,
       to_char(a.UPDATE_TIME, 'YYYY-MM-DD hh24:mi:ss') UPDATE_TIME,
       a.UPDATE_STAFF_ID,
       a.UPDATE_DEPART_ID,
       a.REMARK,
       a.RSRV_NUM1,
       a.RSRV_NUM2,
       a.RSRV_NUM3,
       a.RSRV_STR1,
       a.RSRV_STR2,
       a.RSRV_STR3,
       a.RSRV_STR4,
       a.RSRV_STR5,
       a.RSRV_STR6,
       a.RSRV_STR7,
       a.RSRV_STR8,
       a.RSRV_STR9,
       a.RSRV_STR10,
       a.RSRV_DATE1,
       a.RSRV_DATE2,
       a.RSRV_DATE3,
       a.SERVICE_ID
  FROM TF_F_RELATION_XXT a
 WHERE 1=1
      AND (a.SERIAL_NUMBER_A = :SERIAL_NUMBER_A
      OR a.SERIAL_NUMBER_B = :SERIAL_NUMBER_A)
      AND SYSDATE BETWEEN a.START_DATE AND a.END_DATE
      ORDER BY a.serial_number_a ASC, a.serial_number_b ASC, a.rsrv_str1 ASC, a.rsrv_str2 DESC