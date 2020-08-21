SELECT COUNT(*) recordcount
  FROM tf_b_trade
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(substrb(:TRADE_ID,5,2))
   AND rsrv_str2=:RSRV_STR2