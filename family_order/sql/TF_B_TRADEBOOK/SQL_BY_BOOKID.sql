SELECT T.TRADE_ID,
       T.SERIAL_NUMBER,
       T.BOOK_TYPE,
       T.BOOK_TYPE_CODE,
       T.TRADE_DEPART_ID,
       T.BOOK_STATUS,
       T.IN_MOD_CODE,
       T.TRADE_STAFF_ID,
       T.REMARK,
       TO_CHAR(T.BOOK_DATE, 'YYYY-MM-DD HH24:mi:ss') AS BOOK_DATE,
       TO_CHAR(T.BOOK_END_DATE, 'YYYY-MM-DD HH24:mi:ss') AS BOOK_END_DATE,
       TO_CHAR(T.RSRV_DATE1, 'YYYY-MM-DD HH24:mi:ss') AS DEAL_DATE,
       TO_CHAR(T.RSRV_DATE2, 'YYYY-MM-DD HH24:mi:ss') AS ACCEPT_DATE,
       T.PSPT_TYPE_CODE,
       T.PSPT_ID,
       T.CUST_NAME,
       T.CONTACT_PHONE,
       T.BOOK_PHONE,
       T.GOODS_ID,
       T.DOOR_END_DATE,
       T.TRADE_CITY_CODE,
       T.TRADE_EPARCHY_CODE
  FROM TF_B_TRADE_BOOK T
 WHERE 1 = 1
   AND T.BOOK_TYPE_CODE = :BOOK_TYPE_CODE
   AND T.TRADE_ID = :TRADE_ID
   AND T.ACCEPT_MONTH = :ACCEPT_MONTH
   AND T.SERIAL_NUMBER = :SERIAL_NUMBER
   AND T.TRADE_DEPART_ID = :TRADE_DEPART_ID
   AND T.BOOK_DATE <= TO_DATE(:BOOK_END_DATE, 'YYYY-MM-DD hh24:mi:ss')
   AND T.BOOK_END_DATE >= TO_DATE(:BOOK_DATE, 'YYYY-MM-DD HH24:mi:ss')
   AND T.BOOK_STATUS = :BOOK_STATUS
 ORDER BY BOOK_DATE DESC