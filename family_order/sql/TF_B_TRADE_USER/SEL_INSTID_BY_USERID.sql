SELECT A.SERIAL_NUMBER, B.SUBS_ID, A.USER_STATE_CODESET USER_STATE
  FROM TF_B_TRADE_USER A, TF_F_INSTANCE_PF B
 WHERE A.USER_ID = B.USER_ID
   AND A.USER_ID = :USER_ID
   AND A.TRADE_ID = :TRADE_ID
   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))