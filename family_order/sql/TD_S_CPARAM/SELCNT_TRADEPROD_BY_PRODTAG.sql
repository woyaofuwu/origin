SELECT COUNT(1) recordcount
  FROM tf_b_trade_product
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND ((product_id = :PRODUCT_ID AND modify_tag = :MODIFY_TAG) OR (old_product_id=:PRODUCT_ID AND :MODIFY_TAG='1'))
----注:台帐表中时没有modify_tag＝'1'的记录的