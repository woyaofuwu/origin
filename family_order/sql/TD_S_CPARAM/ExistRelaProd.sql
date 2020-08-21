SELECT COUNT(*) recordcount
FROM   tf_b_trade_relation a, td_b_product_relation b
WHERE  a.trade_id = TO_NUMBER(:TRADE_ID)
and    a.accept_month = TO_NUMBER(substr(:TRADE_ID, 5,2))
AND    a.relation_type_code != b.relation_type_code
AND    b.product_id = :PRODUCT_ID
AND    (a.modify_tag = :MODIFY_TAG OR :MODIFY_TAG = '*')