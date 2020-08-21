SELECT COUNT(1) recordcount
FROM tf_b_trade_relation a
WHERE trade_id = :TRADE_ID
AND accept_month = :ACCEPT_MONTH
AND relation_type_code = :RELATION_TYPE_CODE
AND modify_tag = :MODIFY_TAG
AND (role_code_b = :ROLE_CODE_B OR :ROLE_CODE_B = '*')
AND EXISTS (SELECT 1 FROM tf_f_user_purchase
 WHERE user_id=a.id_b
   AND (purchase_mode=:PURCHASE_MODE OR :PURCHASE_MODE='**')
   AND process_tag=:PROCESS_TAG
   AND end_date > sysdate)