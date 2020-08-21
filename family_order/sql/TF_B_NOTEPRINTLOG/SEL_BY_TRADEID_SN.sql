SELECT A.PRINT_ID,
       A.TRADE_ID,
       A.TEMPLET_CODE,
       A.TEMPLET_TYPE,
       A.NOTE_NO,
       A.TAX_NO,
       A.SOURCE_TYPE,
       A.SERIAL_NUMBER,
       A.ACCT_ID,
       A.PAY_NAME,
       A.PRINT_MODE,
       A.START_CYCLE_ID,
       A.END_CYCLE_ID,
       A.TRADE_TIME,
       A.TRADE_STAFF_ID,
       A.TRADE_DEPART_ID,
       A.TRADE_CITY_CODE,
       A.TRADE_EPARCHY_CODE,
       A.TRADE_REASON_CODE,
       A.TOTAL_FEE,
       A.REPRINT_FLAG,
       A.PRINTED_FEE,
       A.SPECITEM_PRINTFLAG,
       DECODE(NVL(A.PREPRINT_FLAG,'0'),'0','未打印','已打印') PREPRINT_FLAG,
       A.REMARK,
       A.CANCEL_TAG,
       A.EPARCHY_CODE,
       A.CANCEL_TIME,
       A.CANCEL_STAFF_ID,
       A.CANCEL_DEPART_ID,
       A.CANCEL_CITY_CODE,
       A.CANCEL_EPARCHY_CODE,
       A.CANCEL_REASON_CODE,
       A.RSRV_FEE1,
       A.RSRV_FEE2,
       A.RSRV_INFO1,
       A.RSRV_INFO2,
       A.RSRV_INFO3,
       A.RSRV_INFO4,
       A.RSRV_INFO5,
       T.TRADE_TYPE_CODE
  FROM TF_B_NOTEPRINTLOG A,
       (SELECT C.TRADE_ID,
               C.CUST_NAME,
               C.TRADE_TYPE_CODE,
               C.ACCEPT_DATE,
               C.TRADE_STAFF_ID,
               C.TRADE_DEPART_ID,
               C.EPARCHY_CODE
          FROM TF_B_TRADE C
         WHERE C.SERIAL_NUMBER = :SERIAL_NUMBER
           AND C.CANCEL_TAG = :CANCEL_TAG
        UNION ALL
        SELECT B.TRADE_ID,
               B.CUST_NAME,
               B.TRADE_TYPE_CODE,
               B.ACCEPT_DATE,
               B.TRADE_STAFF_ID,
               B.TRADE_DEPART_ID,
               B.EPARCHY_CODE
          FROM TF_BH_TRADE B
         WHERE SERIAL_NUMBER = :SERIAL_NUMBER
           AND B.CANCEL_TAG = :CANCEL_TAG) T
 WHERE A.TRADE_ID = T.TRADE_ID
   AND (A.PREPRINT_FLAG <> '1' or a.preprint_flag is null)
   AND A.TRADE_ID IN (:TRADE_ID)
 ORDER BY A.TRADE_ID