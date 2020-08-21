SELECT trade_id,accept_month,fee_mode,0 fee_type_code,sum(oldfee) oldfee,sum(fee) fee
  FROM tf_b_tradefee_sub
 WHERE trade_id=:TRADE_ID
   AND accept_month=:ACCEPT_MONTH
 GROUP BY trade_id,accept_month,fee_mode