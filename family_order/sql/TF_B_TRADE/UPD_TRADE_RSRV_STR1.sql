UPDATE tf_b_trade
   SET RSRV_STR1=:RSRV_STR1
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
     and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))