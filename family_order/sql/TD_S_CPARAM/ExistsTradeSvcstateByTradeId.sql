SELECT COUNT(*) recordcount
  FROM tf_b_trade_svcstate
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)