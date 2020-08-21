SELECT COUNT(1) recordcount
FROM   dual
WHERE  (SELECT SUM(counts)
        FROM   (SELECT COUNT(1) counts
                 FROM   tf_b_trade_discnt
                 WHERE  trade_id = :TRADE_ID
                 and    accept_month = to_number(substr(:TRADE_ID, 5, 2))
                 AND    discnt_code = :DISCNT_CODE
                 AND    user_ID <> :USER_ID
                 AND    modify_tag = '0'
                 UNION
                 SELECT (-1) * COUNT(1) counts
                 FROM   tf_b_trade_discnt
                 WHERE  trade_id = :TRADE_ID
                 and    accept_month = to_number(substr(:TRADE_ID, 5, 2))
                 AND    discnt_code = :DISCNT_CODE
                 AND    user_ID <> :USER_ID
                 AND    modify_tag = '1'
                 UNION
                 SELECT COUNT(1) counts
                 FROM   tf_f_user_discnt
                 WHERE  user_id_a = :USER_ID_A
                 AND    user_id <> :USER_ID
                 AND    end_date >= add_months(trunc(sysdate, 'mm'), 1))) > :NUM