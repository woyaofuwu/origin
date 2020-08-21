DELETE FROM tf_b_trade_res
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND res_type_code = 'S'