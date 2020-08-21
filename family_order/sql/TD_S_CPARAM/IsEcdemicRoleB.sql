SELECT COUNT(1) recordcount
FROM   tf_b_trade_relation a
WHERE  a.trade_id = :TRADE_ID
and    a.accept_month = to_number(substr(:TRADE_ID, 5, 2))
AND    a.relation_type_code = :RELATION_TYPE_CODE
AND    a.role_code_b = '2'
AND    a.modify_tag = '0'
AND    EXISTS (SELECT 1
        FROM   tf_f_user
        WHERE  a.user_id_b = user_id
        AND    partition_id = MOD(a.user_id_b, 10000)
        AND    eparchy_code = :EPARCHY_CODE)