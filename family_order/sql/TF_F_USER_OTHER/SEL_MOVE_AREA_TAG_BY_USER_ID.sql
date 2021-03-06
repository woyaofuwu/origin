SELECT PARTITION_ID,
       TO_CHAR(USER_ID) USER_ID,
       RSRV_VALUE_CODE,
       RSRV_VALUE,
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
       RSRV_STR11,
       RSRV_STR12,
       RSRV_STR13,
       RSRV_STR14,
       RSRV_STR15,
       RSRV_STR16,
       RSRV_STR17,
       RSRV_STR18,
       RSRV_STR19,
       RSRV_STR20,
       TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,
       TO_CHAR(END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,
       UPDATE_DEPART_ID,
       UPDATE_STAFF_ID,
       UPDATE_TIME,
       TRADE_ID,
       DEPART_ID,
       STAFF_ID,
       PROCESS_TAG,
       REMARK,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3,
       RSRV_TAG4,
       RSRV_TAG5,
       RSRV_TAG6,
       RSRV_TAG7,
       RSRV_TAG8,
       RSRV_TAG9,
       RSRV_TAG10,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       RSRV_NUM4,
       RSRV_NUM5,
       RSRV_NUM6,
       RSRV_NUM7,
       RSRV_NUM8,
       RSRV_NUM9,
       RSRV_NUM10,
       TO_CHAR(RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,
       TO_CHAR(RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,
       TO_CHAR(RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,
       TO_CHAR(RSRV_DATE4, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE4,
       TO_CHAR(RSRV_DATE5, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE5,
       TO_CHAR(RSRV_DATE6, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE6,
       TO_CHAR(RSRV_DATE7, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE7,
       TO_CHAR(RSRV_DATE8, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE8,
       TO_CHAR(RSRV_DATE9, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE9,
       TO_CHAR(RSRV_DATE10, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE10,
       INST_ID
  FROM TF_F_USER_OTHER
 WHERE RSRV_VALUE_CODE = :RSRV_VALUE_CODE
   AND USER_ID = :USER_ID
   AND SYSDATE BETWEEN START_DATE AND END_DATE