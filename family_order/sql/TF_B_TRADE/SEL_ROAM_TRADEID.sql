SELECT trade_id FROM tf_b_trade WHERE exec_time>SYSDATE
AND user_id=:USER_ID AND trade_type_code IN (110,111)