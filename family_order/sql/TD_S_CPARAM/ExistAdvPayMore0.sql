SELECT COUNT(*) recordcount
  FROM tf_b_trade
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND cancel_tag = '0'
   AND advance_pay > 0