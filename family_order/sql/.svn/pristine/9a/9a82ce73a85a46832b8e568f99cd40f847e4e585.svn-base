SELECT COUNT(1) recordcount
FROM tf_b_trade_relation
WHERE trade_id = :TRADE_ID
AND accept_month = :ACCEPT_MONTH
AND relation_type_code = :RELATION_TYPE_CODE
AND (modify_tag = :MODIFY_TAG OR :MODIFY_TAG = '*')
AND role_code_b = :ROLE_CODE_B