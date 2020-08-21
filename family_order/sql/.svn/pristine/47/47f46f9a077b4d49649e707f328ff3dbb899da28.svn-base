SELECT *
FROM tf_b_trade t
WHERE t.trade_type_code=:TRADE_TYPE_CODE and t.user_id=:USER_ID
AND t.exec_time<SYSDATE
AND t.subscribe_state NOT IN ('5','9','A')