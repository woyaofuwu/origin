SELECT COUNT(1) recordcount
FROM tf_b_trade WHERE trade_type_code IN (286,288)
AND eparchy_code=:EPARCHY_CODE AND rsrv_str10=:TICKET_ID
AND trade_id <> :TRADE_ID