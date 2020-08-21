UPDATE tf_b_trade_discnt
   SET start_date=TRUNC(SYSDATE)  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))