SELECT COUNT(1) recordcount FROM dual
WHERE (
SELECT COUNT(1) FROM tf_b_trade_relation
WHERE trade_id = :TRADE_ID
AND relation_type_code = :RELATION_TYPE_CODE
AND end_date > SYSDATE
AND (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*')) > :NUM