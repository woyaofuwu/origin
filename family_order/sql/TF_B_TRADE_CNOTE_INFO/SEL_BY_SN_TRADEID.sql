SELECT TO_CHAR(C.TRADE_ID) TRADE_ID,
       C.ACCEPT_MONTH,
       C.RECEIPT_INFO1,
       C.RECEIPT_INFO2,
       C.RECEIPT_INFO3,
       C.RECEIPT_INFO4,
       C.RECEIPT_INFO5,
       C.NOTICE_CONTENT,
       T.TRADE_TYPE_CODE PRIORITY,
       C.TRADE_STAFF_ID STAFF_ID,
       C.TRADE_DEPART_ID DEPART_ID,
       C.ACCEPT_DATE BRAND,
       SUBSTR(T.PROCESS_TAG_SET, 20, 1) VIP_CLASS,
       T.CUST_NAME
  FROM TF_B_TRADE_CNOTE_INFO C, TD_S_TRADETYPE D,
       (SELECT TRADE_ID,
               ACCEPT_MONTH,
               SERIAL_NUMBER,
               CUST_NAME,
               TRADE_TYPE_CODE,
               ACCEPT_DATE,
               TRADE_STAFF_ID,
               TRADE_DEPART_ID,
               EPARCHY_CODE,
               PROCESS_TAG_SET
          FROM TF_B_TRADE
         WHERE SERIAL_NUMBER = :SERIAL_NUMBER
           AND TRADE_STAFF_ID = :TRADE_STAFF_ID OR :TRADE_STAFF_ID IS NULL
           AND ACCEPT_DATE > TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
           AND ACCEPT_DATE < TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))
           AND CANCEL_TAG = :CANCEL_TAG
        UNION ALL
        SELECT TRADE_ID,
               ACCEPT_MONTH,
               SERIAL_NUMBER,
               CUST_NAME,
               TRADE_TYPE_CODE,
               ACCEPT_DATE,
               TRADE_STAFF_ID,
               TRADE_DEPART_ID,
               EPARCHY_CODE,
               PROCESS_TAG_SET
          FROM TF_BH_TRADE
         WHERE SERIAL_NUMBER = :SERIAL_NUMBER
           AND TRADE_STAFF_ID = :TRADE_STAFF_ID OR :TRADE_STAFF_ID IS NULL
           AND ACCEPT_DATE > TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
           AND ACCEPT_DATE < TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))
           AND CANCEL_TAG = :CANCEL_TAG) T
 WHERE C.TRADE_ID = T.TRADE_ID
   AND T.TRADE_TYPE_CODE = D.TRADE_TYPE_CODE
   AND T.EPARCHY_CODE = D.EPARCHY_CODE
   AND C.TRADE_ID IN (:TRADE_ID)
 ORDER BY C.TRADE_ID