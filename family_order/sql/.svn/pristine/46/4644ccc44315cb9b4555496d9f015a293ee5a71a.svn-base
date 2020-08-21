SELECT COUNT(1) recordcount FROM tf_b_trade_product a
WHERE trade_id = :TRADE_ID
AND EXISTS (SELECT 1 FROM tf_b_trade_discnt
            WHERE trade_id = :TRADE_ID
            AND decode(modify_tag, '4', '0','5','1', modify_tag) = '0'
            AND trunc(start_date)!=trunc(a.start_date))