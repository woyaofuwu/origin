SELECT COUNT(1) recordcount
FROM   tf_b_trade_relation a
WHERE  trade_id = :TRADE_ID
AND    accept_month = :ACCEPT_MONTH
AND    modify_tag = :MODIFY_TAG
AND    role_code_b = :ROLE_CODE_B
AND    EXISTS
 (SELECT 1
        FROM   tf_f_user_discnt b
        WHERE  b.user_id = a.user_id_b
        and    b.partition_id = mod(a.user_id_b,10000)
        AND    b.end_date > SYSDATE
        AND    b.discnt_code IN (SELECT para_code1
                                 FROM   td_s_commpara
                                 WHERE  subsys_code = 'CSM'
                                 AND    param_attr = 2001
                                 AND    param_code = :PARAM_CODE_B
                                 AND    (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
                                 AND    SYSDATE BETWEEN start_date AND end_date)
        AND    EXISTS (SELECT 1
                FROM   tf_f_user_discnt c
                WHERE  b.user_id_a = c.user_id_a
                AND    b.relation_type_code = c.relation_type_code
                AND    c.user_id = :USER_ID
                AND    c.end_date > SYSDATE
                AND    c.discnt_code IN
                       (SELECT para_code1
                         FROM   td_s_commpara
                         WHERE  subsys_code = 'CSM'
                         AND    param_attr = 2001
                         AND    param_code = :PARAM_CODE_A
                         AND    (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
                         AND    SYSDATE BETWEEN start_date AND end_date)))