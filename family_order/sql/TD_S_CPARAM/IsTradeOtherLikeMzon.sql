Select COUNT(1) recordcount
FROM tf_b_trade_sale_active  a ,td_b_package b
WHERE trade_id=TO_NUMBER(:TRADE_ID)
  AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  And a.package_id=b.package_id
  And (b.package_kind_code  =:RSRV_STR1 OR :RSRV_STR1 IS NULL OR b.package_kind_code  like :RSRV_STR1)
  And (modify_tag=:MODIFY_TAG OR :MODIFY_TAG='*')