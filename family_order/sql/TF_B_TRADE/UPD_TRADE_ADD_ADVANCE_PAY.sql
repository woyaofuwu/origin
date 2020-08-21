UPDATE tf_b_trade SET advance_pay=TO_NUMBER(:ADVANCE_PAY)
WHERE trade_id = :TRADE_ID