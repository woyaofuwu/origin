SELECT COUNT(1) recordcount FROM tf_b_trade_relation a
WHERE trade_id = :TRADE_ID
AND accept_month = :ACCEPT_MONTH
AND relation_type_code = :RELATION_TYPE_CODE
AND modify_tag = :MODIFY_TAG
AND role_code_b = '2'
AND EXISTS (SELECT 1 FROM tf_b_trade
            WHERE user_id = a.id_b
            AND trade_type_code = 110
            AND exec_time > sysdate)