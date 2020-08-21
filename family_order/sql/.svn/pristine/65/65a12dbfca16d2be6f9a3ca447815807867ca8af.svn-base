SELECT COUNT(1) recordcount
FROM   dual
WHERE  (SELECT SUM(a)
        FROM   (SELECT COUNT(1) a
                 FROM   tf_b_trade_relation
                 WHERE  trade_id = :TRADE_ID
                 and    accept_month = to_number(substr(:TRADE_ID, 5, 2))
                 AND    relation_type_code = :RELATION_TYPE_CODE
                 AND    modify_tag = '0'
                 AND    (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*')
                 union all
                 SELECT COUNT(1) a
                 FROM   tf_b_trade_relation
                 WHERE  trade_id = :TRADE_ID
                 and    accept_month = to_number(substr(:TRADE_ID, 5, 2))
                 AND    relation_type_code = :RELATION_TYPE_CODE
                 AND    modify_tag = '2'
                 AND    (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*'))) IN (:NUM, 0)