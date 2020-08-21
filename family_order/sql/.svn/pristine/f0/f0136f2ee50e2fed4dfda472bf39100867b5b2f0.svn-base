UPDATE tf_bh_trade  SET oper_fee =to_number(:ADJUST_FEE)
 WHERE  trade_id =:TRADE_ID  
 AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))