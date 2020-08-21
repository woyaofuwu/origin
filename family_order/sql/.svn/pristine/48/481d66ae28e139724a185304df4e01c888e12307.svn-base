DELETE FROM tf_f_user_svcstate c WHERE c.partition_id = MOD(to_number(:USER_ID), 10000) AND c.User_Id = :USER_ID
AND EXISTS (SELECT 1 FROM tf_b_trade_svcstate_bak d WHERE d.trade_id = :TRADE_ID AND d.user_id = c.user_id
AND d.service_id = c.service_id AND d.state_code = c.state_code AND d.start_date = c.start_date)