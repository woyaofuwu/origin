SELECT TO_CHAR(B.TRADE_ID) TRADE_ID,
       A.SERIAL_NUMBER,
       A.ACCEPT_MONTH,
       A.USER_ID,
       A.SCORE_TYPE_CODE,
       A.SCORE,
       A.SCORE_CHANGED,
       TO_CHAR(NVL(A.VALUE_CHANGED / 100, 0)) VALUE_CHANGED,
       A.RULE_ID,
       A.RES_ID,
       A.GOODS_NAME,
       A.REMARK,
       B.SERIAL_NUMBER SERIAL_NUMBER_B,
       B.CUST_NAME,
       B.TRADE_TYPE_CODE,
       C.TRADE_TYPE,
       TO_CHAR(B.ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,
       B.TRADE_STAFF_ID,
       DECODE(B.CANCEL_TAG,
              '0','未返销',
              '1','被返销',
              '2','返销') CANCEL_TAG,
       B.CANCEL_DATE,
       B.CANCEL_STAFF_ID,
       B.RSRV_STR5,
       B.RSRV_STR6,
       B.RSRV_STR7
  FROM TF_B_TRADE_SCORE A, TF_B_TRADE B, TD_S_TRADETYPE C
 WHERE A.TRADE_ID = B.TRADE_ID(+)
   AND B.TRADE_TYPE_CODE = C.TRADE_TYPE_CODE
   AND (B.EPARCHY_CODE = :EPARCHY_CODE OR B.EPARCHY_CODE IS NULL) 
   AND (C.TRADE_TYPE_CODE =:TRADE_TYPE_CODE  or :TRADE_TYPE_CODE is null)
   AND C.EPARCHY_CODE = :EPARCHY_CODE
   AND A.SERIAL_NUMBER = :SERIAL_NUMBER
   AND B.ACCEPT_DATE BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND
       TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') + 1
UNION ALL
SELECT TO_CHAR(B.TRADE_ID) TRADE_ID,
       A.SERIAL_NUMBER,
       A.ACCEPT_MONTH,
       A.USER_ID,
       A.SCORE_TYPE_CODE,
       A.SCORE,
       A.SCORE_CHANGED,
       TO_CHAR(NVL(A.VALUE_CHANGED / 100, 0)) VALUE_CHANGED,
       A.RULE_ID,
       A.RES_ID,
       A.GOODS_NAME,
       A.REMARK,
       B.SERIAL_NUMBER SERIAL_NUMBER_B,
       B.CUST_NAME,
       B.TRADE_TYPE_CODE,
       C.TRADE_TYPE,
       TO_CHAR(B.ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,
       B.TRADE_STAFF_ID,
       DECODE(B.CANCEL_TAG,
              '0','未返销',
              '1','被返销',
              '2','返销') CANCEL_TAG,
       B.CANCEL_DATE,
       B.CANCEL_STAFF_ID,
       B.RSRV_STR5,
       B.RSRV_STR6,
       B.RSRV_STR7
  FROM TF_B_TRADE_SCORE A, TF_BH_TRADE B, TD_S_TRADETYPE C
 WHERE A.TRADE_ID = B.TRADE_ID(+)
   AND B.TRADE_TYPE_CODE = C.TRADE_TYPE_CODE
   AND (B.EPARCHY_CODE = :EPARCHY_CODE OR B.EPARCHY_CODE IS NULL) 
   AND (C.TRADE_TYPE_CODE =:TRADE_TYPE_CODE  or :TRADE_TYPE_CODE is null)
   AND C.EPARCHY_CODE = :EPARCHY_CODE
   AND A.SERIAL_NUMBER = :SERIAL_NUMBER
   AND B.ACCEPT_DATE+0 BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND
       TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') + 1
   ORDER BY TRADE_ID DESC