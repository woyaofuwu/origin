SELECT TO_CHAR(TRADE_ID) TRADE_ID,
       TO_CHAR(ACCT_ID) ACCT_ID,
       DISCNT_CODE,
       TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,
       TO_CHAR(END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,
       TO_CHAR(INST_ID) INST_ID,
       MODIFY_TAG,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       TO_NUMBER(RSRV_NUM4) RSRV_NUM4,
       TO_NUMBER(RSRV_NUM5) RSRV_NUM5,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       TO_CHAR(RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,
       TO_CHAR(RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,
       TO_CHAR(RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3
  FROM TF_B_TRADE_ACCT_DISCNT T
 WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID)