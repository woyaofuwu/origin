DELETE FROM tf_b_trade_discnt
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND RSRV_TAG1=:RSRV_TAG1
   and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))