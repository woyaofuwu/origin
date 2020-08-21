SELECT COUNT(1) recordcount FROM dual
WHERE (SELECT COUNT(1) FROM tf_f_relation_uu
WHERE relation_type_code = :RELATION_TYPE_CODE
AND user_id_a = :USER_ID_A
AND (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*')
AND end_date > sysdate)
+ (SELECT COUNT(1) FROM tf_b_trade_relation
WHERE trade_id = :TRADE_ID
AND relation_type_code = :RELATION_TYPE_CODE
AND (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*')
AND modify_tag = '0')
- (SELECT COUNT(1) FROM tf_b_trade_relation
WHERE trade_id = :TRADE_ID
AND relation_type_code = :RELATION_TYPE_CODE
AND (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*')
AND modify_tag = '1') > :NUM