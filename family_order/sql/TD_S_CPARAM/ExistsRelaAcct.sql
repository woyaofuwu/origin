SELECT COUNT(1) recordcount FROM tf_b_trade_relation a
WHERE trade_id = :TRADE_ID
AND accept_month = :ACCEPT_MONTH
AND modify_tag = :MODIFY_TAG
AND (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*')
AND EXISTS (SELECT 1 FROM tf_a_payrelation
            WHERE user_id= a.id_b
            AND partition_id = MOD(a.id_b,10000)
            AND acct_id = :ACCT_ID
            AND default_tag = :DEFAULT_TAG
            AND act_tag=:ACT_TAG
            AND (SELECT MIN(acyc_id) FROM td_a_acycpara WHERE use_tag=0)
                        BETWEEN start_acyc_id  AND end_acyc_id)