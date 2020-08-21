--IS_CACHE=Y
SELECT T.TEMPLATE_CONTENT1,T.TEMPLATE_CONTENT2,T.TEMPLATE_CONTENT3,T.TEMPLATE_CONTENT4,T.TEMPLATE_CONTENT5,R.EXPRESSION   
  FROM td_b_trade_receipt R,td_b_template T
 WHERE R.TEMPLATE_ID=T.TEMPLATE_ID 
   AND (R.trade_type_code = :TRADE_TYPE_CODE or R.trade_type_code=-1)
   AND (R.brand_code = :BRAND_CODE OR R.brand_code='ZZZZ')
   AND (R.product_id = :PRODUCT_ID OR R.product_id=-1)
   AND R.trade_attr = :TRADE_ATTR
   AND (R.eparchy_code = :EPARCHY_CODE OR R.eparchy_code='ZZZZ')
   AND sysdate BETWEEN R.start_date AND R.end_date
   ORDER BY R.trade_type_code desc,R.brand_code,R.product_id Desc,R.eparchy_code
