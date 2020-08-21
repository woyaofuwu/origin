INSERT INTO tf_f_user_tradelimit(user_id,trade_type_code,start_date,end_date)
SELECT a.user_id, a.trade_type_code, a.start_date, a.end_date 
FROM tf_b_trade_limit_bak a WHERE a.trade_id = :TRADE_ID