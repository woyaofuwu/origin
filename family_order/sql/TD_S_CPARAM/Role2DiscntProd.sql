SELECT COUNT(1) recordcount
FROM tf_b_trade_discnt a
WHERE a.trade_id = :TRADE_ID
AND a.discnt_code = :DISCNT_CODE
AND a.ID <> :USER_ID
AND a.id_type = '1'
AND a.modify_tag = '0'
AND EXISTS (SELECT 1 FROM tf_f_user
            WHERE user_id = a.ID
            AND partition_id = mod(a.ID,10000)
            AND product_id = :PRODUCT_ID)