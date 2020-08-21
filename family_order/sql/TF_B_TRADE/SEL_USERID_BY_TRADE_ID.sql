SELECT to_char(trade_id) trade_id,to_char(user_id) user_id FROM tf_b_trade
WHERE trade_id = TO_NUMBER(:TRADE_ID)
UNION
SELECT to_char(trade_id) trade_id,to_char(user_id) user_id FROM tf_bh_trade
WHERE trade_id = TO_NUMBER(:TRADE_ID)
UNION
SELECT to_char(charge_id) trade_id,to_char(user_id) user_id FROM tf_a_paylog
WHERE charge_id = TO_NUMBER(:TRADE_ID)