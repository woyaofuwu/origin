SELECT COUNT(1) recordcount FROM tf_b_trade_relation a
WHERE trade_id = :TRADE_ID
AND accept_month = :ACCEPT_MONTH
AND modify_tag = :MODIFY_TAG
AND role_code_b = :ROLE_CODE_B
AND EXISTS (SELECT 1 FROM tf_f_user_discnt
            WHERE user_id = a.user_id_b
            and partition_id = mod(a.user_id_b, 10000)
            AND end_date > SYSDATE
            AND discnt_code IN
                              (SELECT para_code1 FROM td_s_commpara
                                WHERE subsys_code='CSM'
                                  AND param_attr=2001
                                  AND param_code=:PARAM_CODE
                                  AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                                  AND SYSDATE BETWEEN start_date AND end_date))
AND EXISTS (SELECT 1 FROM TF_B_TRADE_DISCNT
            WHERE trade_id = :TRADE_ID
             and  accept_month = to_number(substr(:TRADE_ID,5,2))
             AND  user_id = a.user_id_b
             AND  discnt_code IN (SELECT para_code1 FROM td_s_commpara
                                  WHERE subsys_code='CSM'
                                  AND param_attr=2001
                                  AND param_code=:PARAM_CODE1
                                  AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                                  AND SYSDATE BETWEEN start_date AND end_date))