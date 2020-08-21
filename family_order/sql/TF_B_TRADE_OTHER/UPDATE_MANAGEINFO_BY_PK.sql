update tf_b_trade_other T SET T.IS_NEED_PF= :IS_NEED_PF
 WHERE trade_id=TO_NUMBER(:TRADE_ID)  
  AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND RSRV_STR1=:RSRV_STR1