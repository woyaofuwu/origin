

SELECT CARD_NO,
       CARD_KIND_CODE,
       CARD_CHANGE_LEVEL,
       NVL(CARD_FEE, 0) CARD_FEE,
       CARD_STATE_CODE,
       CHANGE_SERIAL_NUMBER,
       TO_CHAR(CHANGE_TIME, 'yyyy-mm-dd hh24:mi:ss') CHANGE_TIME,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3,
       REMARK
  FROM TF_R_KKKL_GGCARD A
 WHERE A.CARD_NO = :CARD_NO
   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE
