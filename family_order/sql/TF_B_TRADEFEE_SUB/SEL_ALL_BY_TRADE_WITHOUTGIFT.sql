SELECT to_char(trade_id) trade_id,accept_month,fee_mode,fee_type_code,to_char(oldfee) oldfee,to_char(fee) fee,to_char(charge_id) charge_id
  FROM tf_b_tradefee_sub
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))