SELECT COUNT(1) recordcount FROM tf_b_trade_relation a
WHERE trade_id = :TRADE_ID
AND accept_month = :ACCEPT_MONTH
AND relation_type_code = :RELATION_TYPE_CODE
AND modify_tag = :MODIFY_TAG
AND (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*')
AND EXISTS (SELECT 1 FROM tf_f_relation_uu
            WHERE user_id_b = a.id_b
            AND partition_id = MOD(a.id_b,10000)
            AND end_date > SYSDATE
            AND serial_number_a IN (SELECT para_code1 FROM td_s_commpara
              WHERE subsys_code='CSM'
                AND param_attr=2001
                AND param_code=:PARAM_CODE
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date))