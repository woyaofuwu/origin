SELECT *
  FROM tf_b_tradefee_sub a
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND fee_mode = '1'
   AND fee > 0
