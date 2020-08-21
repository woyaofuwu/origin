DELETE FROM ti_b_user_discnt a
WHERE EXISTS (SELECT 1 FROM tf_b_trade_discnt WHERE trade_id = to_number(:TRADE_ID) AND ID = a.user_id)