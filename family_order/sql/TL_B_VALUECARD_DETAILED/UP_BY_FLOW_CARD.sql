UPDATE TL_B_VALUECARD_DETAILED A
   SET A.CUST_NUMBER     = :SERIAL_NUMBER,
       A.DISCNT_TRADE_ID = :TRADE_ID,
       A.STATE_NAME      = '充值',
       A.STATE_CODE      = '3',
       A.UPDATE_TIME     = TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS')
 WHERE A.CARD_NUMBER = :FLOW_CARD