SELECT to_char(trade_id) trade_id,accept_month,res_type_code,res_code
  FROM tf_b_trade_res
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag=:MODIFY_TAG