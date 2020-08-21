SELECT COUNT(1) recordcount FROM(
 SELECT COUNT(user_id_a) recordcount FROM tf_f_relation_uu
 WHERE user_id_b IN
    (SELECT user_id_b FROM tf_b_trade_relation
    WHERE trade_id = :TRADE_ID
    AND accept_month = :ACCEPT_MONTH
    AND modify_tag = :MODIFY_TAG
    AND (role_code_b = :ROLE_CODE_1 OR :ROLE_CODE_1 = '*')
    UNION ALL
    SELECT user_id_b id_b FROM tf_f_relation_uu
    WHERE user_id_a = :USER_ID_A
    AND end_date+0 > trunc(last_day(SYSDATE)+1))
 AND (role_code_b = :ROLE_CODE_2 OR :ROLE_CODE_2 = '*')
 AND (instr(:RELATION_TYPE_CODE,relation_type_code)>0 OR relation_type_code LIKE :RELATION_TYPE_CODE OR :RELATION_TYPE_CODE='*')
 AND user_id_a != :USER_ID_A
 AND end_date+0 > trunc(last_day(SYSDATE)+1)
 GROUP BY user_id_a
 HAVING COUNT(user_id_a)>:NUM)