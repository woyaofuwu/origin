INSERT INTO tf_b_tradefee_paymoney
  (TRADE_ID,
   ACCEPT_MONTH,
   PAY_MONEY_CODE,
   MONEY,
   UPDATE_TIME,
   UPDATE_STAFF_ID,
   UPDATE_DEPART_ID)
  SELECT TO_NUMBER(:NEWTRADE_ID),
         TO_NUMBER(SUBSTR(:NEWTRADE_ID, 5, 2)),
         PAY_MONEY_CODE,
         :ADJUST_FEE,
         SYSDATE,
         :STAFF_ID,
         :DEPART_ID
    FROM tf_b_tradefee_paymoney
   WHERE trade_id = :TRADE_ID