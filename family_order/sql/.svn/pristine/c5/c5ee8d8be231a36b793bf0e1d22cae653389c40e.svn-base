SELECT A.UPD_LOG_ID,
       A.CYCLY_ID,
       TO_CHAR(A.USER_ID) USER_ID,
       A.SERIAL_NUMBER,
       A.ELEMENT_TYPE_CODE,
       A.OLD_ELEMENT_ID,
       A.UPD_ELEMENT_ID,
       to_char(A.UPD_DATE, 'yyyy-mm-dd hh24:mi:ss') UPD_DATE,
       A.DEAL_TAG,
       A.DEAL_RESULT,
       to_char(A.DEAL_TIME, 'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,
       A.TRADE_ID,
       A.SYNC_ID,
       A.REMARK,
       A.RSRV_STR1,
       A.RSRV_STR2,
       A.RSRV_STR3,
       A.RSRV_NUMBER1,
       A.RSRV_NUMBER2,
       A.RSRV_NUMBER3,
       to_char(A.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(A.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(A.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3
  FROM TF_FH_ELEMENT_UPD A,
       (SELECT MIN(DEAL_TIME) DEAL_TIME
          FROM TF_FH_ELEMENT_UPD
         WHERE DEAL_TAG = :DEAL_TAG
           AND USER_ID = :USER_ID) B
 WHERE A.USER_ID = :USER_ID
   AND A.DEAL_TIME = B.DEAL_TIME