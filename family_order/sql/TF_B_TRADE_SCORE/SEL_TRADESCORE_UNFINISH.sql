select a.* from tf_b_trade a,tf_b_trade_score b
where a.trade_id = b.trade_id
and a.user_id = :USER_ID 
and a.trade_type_code <>'110'