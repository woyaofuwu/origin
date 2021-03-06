SELECT PARTITION_ID,
       TO_CHAR(USER_ID) USER_ID,
       SERVICE_ID,
       BIZ_STATE_CODE,
       INST_ID,
       TO_CHAR(FIRST_DATE, 'YYYY-MM-DD HH24:MI:SS') FIRST_DATE,
       TO_CHAR(FIRST_DATE_MON, 'YYYY-MM-DD HH24:MI:SS') FIRST_DATE_MON,
       GIFT_SERIAL_NUMBER,
       GIFT_USER_ID,
       TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,
       TO_CHAR(END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,
       TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       TO_CHAR(RSRV_NUM4) RSRV_NUM4,
       TO_CHAR(RSRV_NUM5) RSRV_NUM5,
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
       TO_CHAR(RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,
       TO_CHAR(RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,
       TO_CHAR(RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3
  FROM TF_F_USER_PLATSVC A
 WHERE 1 = 1
   AND A.USER_ID = :USER_ID
   AND A.PARTITION_ID = mod(TO_NUMBER(A.USER_ID),10000)
   AND A.SERVICE_ID = :SERVICE_ID
   AND A.END_DATE > SYSDATE