--IS_CACHE=Y
SELECT  TEMPLATE_ID  
  FROM td_b_trade_receipt
 WHERE (trade_type_code = :TRADE_TYPE_CODE or trade_type_code=-1)
   AND (brand_code = :BRAND_CODE OR brand_code='ZZZZ')
   AND (product_id = :PRODUCT_ID OR product_id=-1)
   AND trade_attr = :TRADE_ATTR
   AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
   AND sysdate BETWEEN start_date AND end_date
   ORDER BY trade_type_code desc,brand_code,product_id Desc,eparchy_code
