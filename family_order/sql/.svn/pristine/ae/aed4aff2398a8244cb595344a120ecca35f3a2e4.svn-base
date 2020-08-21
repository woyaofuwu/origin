INSERT INTO tf_f_user_score(partition_id,user_id,score_type_code,score_value)
SELECT MOD(user_id,10000), user_id, score_type_code, score_value FROM tf_b_trade_score_bak
 WHERE trade_id = :TRADE_ID