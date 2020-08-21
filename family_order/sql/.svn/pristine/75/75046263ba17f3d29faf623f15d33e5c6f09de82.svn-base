SELECT COUNT(1) recordcount FROM tf_b_trade_relation a 
 WHERE trade_id = :TRADE_ID
 AND accept_month = :ACCEPT_MONTH
 AND modify_tag = :MODIFY_TAG
 AND (role_code_b = :ROLE_CODE_1 OR :ROLE_CODE_1 = '*')
 AND EXISTS (SELECT 1 FROM tf_f_relation_uu
            WHERE user_id_b = a.user_id_b
            AND partition_id = MOD(a.user_id_b,10000)
            AND end_date+0 > trunc(last_day(SYSDATE)+1)
            AND (role_code_b = :ROLE_CODE_2 OR :ROLE_CODE_2 = '*')
            AND (instr(:RELATION_TYPE_CODE,relation_type_code)>0 OR relation_type_code LIKE :RELATION_TYPE_CODE OR :RELATION_TYPE_CODE='*'))