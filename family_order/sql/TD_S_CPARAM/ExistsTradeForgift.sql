SELECT count(1) recordcount
  FROM tf_b_tradefee_sub
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND fee_mode = :FEE_MODE
   AND fee_type_code = :FEE_TYPE_CODE
   AND fee > 0