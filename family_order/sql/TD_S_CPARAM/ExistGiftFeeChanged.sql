SELECT count(1) recordcount
  FROM tf_b_tradefee_giftfee
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND (fee_mode = :FEE_MODE OR :FEE_MODE = '*')