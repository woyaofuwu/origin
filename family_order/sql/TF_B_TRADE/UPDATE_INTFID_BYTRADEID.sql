UPDATE tf_b_trade 
SET intf_id = :INTF_ID
WHERE trade_id = TO_NUMBER(:TRADE_ID)
