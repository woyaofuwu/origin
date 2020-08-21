--IS_CACHE=Y
SELECT a.PRODUCT_ID paracode,a.PRODUCT_NAME paraname
  FROM td_b_product a
 WHERE sysdate BETWEEN a.start_date AND a.end_date
   AND a.brand_code = 'FAML'
   AND EXISTS (SELECT 1 FROM td_b_product_release b WHERE b.product_id = a.product_id AND SYSDATE BETWEEN b.start_date AND b.end_date AND (b.release_eparchy_code = :TRADE_EPARCHY_CODE OR b.release_eparchy_code = 'ZZZZ'))