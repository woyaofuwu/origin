SELECT COUNT(1) recordcount
FROM   tf_b_trade a
WHERE  trade_id = TO_NUMBER(:TRADE_ID)
AND    rsrv_str2 = '1' --新增邮寄帐单用户
AND    (NOT EXISTS (SELECT 1
                    FROM   tf_f_user_brandchange
                    WHERE  user_id = a.user_id
                    and    partition_id = mod(a.user_id, 10000)
                    AND    SYSDATE BETWEEN start_date AND end_date
                    AND    rsrv_str1 = '1') --全球通用户
       AND NOT EXISTS (SELECT 1
                        FROM   tf_b_trade
                        WHERE  trade_id <> TO_NUMBER(:TRADE_ID)
                        AND    user_id = a.user_id
                        AND    trade_type_code in (110, 111)
                        AND    to_char(product_id) in
                               (SELECT para_code1
                                 FROM   td_s_commpara
                                 WHERE  param_attr = 102
                                 AND    param_code = 'GoToneP'
                                 AND    SYSDATE BETWEEN start_date AND end_date)
                        AND    exec_time >= SYSDATE) --全球通用户
      )