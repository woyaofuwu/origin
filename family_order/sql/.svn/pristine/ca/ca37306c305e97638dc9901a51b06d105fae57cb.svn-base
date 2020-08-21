select to_char(INST_ID) INST_ID,
       PARTITION_ID,
       SERIAL_NUMBER,
       TO_CHAR(SHARE_ID) SHARE_ID,
       TO_CHAR(USER_ID_B) USER_ID_B,
       EPARCHY_CODE,
       ROLE_CODE,
       to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       RSRV_TAG1,
       RSRV_TAG2
  from TF_F_USER_SHARE_RELA
 WHERE INST_ID = :INST_ID
   AND SYSDATE < END_DATE
   AND START_DATE < END_DATE