INSERT INTO tf_bh_trade_detail 
SELECT * FROM tf_b_trade_detail WHERE trade_id=TO_NUMBER(:TRADE_ID)
AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))