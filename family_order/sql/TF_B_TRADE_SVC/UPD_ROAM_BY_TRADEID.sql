UPDATE tf_b_trade_svc
    SET modify_tag='4'
    WHERE trade_id=TO_NUMBER(:TRADE_ID)
    AND service_id BETWEEN 13 AND 19