SELECT COUNT(1) recordcount FROM tf_b_trade a
WHERE trade_id = :TRADE_ID
AND EXISTS (SELECT 1 FROM tf_b_trade_discnt
            WHERE trade_id = :TRADE_ID
            AND modify_tag = '0'
            AND trunc(start_date)!=trunc(a.exec_time))