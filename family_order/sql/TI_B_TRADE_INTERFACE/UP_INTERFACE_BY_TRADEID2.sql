UPDATE TI_B_TRADE_INTERFACE A
   SET A.SUBSCRIBE_STATE = :SUBSCRIBE_STATE,
       A.EXEC_RESULT     = :EXEC_RESULT,
       A.EXEC_DESC       = :EXEC_DESC,
       A.UPDATE_TIME     = SYSDATE
 WHERE A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   AND A.TRADE_ID = :TRADE_ID