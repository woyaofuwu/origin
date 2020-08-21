DELETE FROM tf_f_user_score a
 WHERE a.user_id=TO_NUMBER(:USER_ID)
AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
AND EXISTS (SELECT 1 FROM tf_b_trade_score_bak b WHERE a.user_id=b.user_id and a.score_type_code=b.score_type_code and b.trade_id=TO_NUMBER(:TRADE_ID))