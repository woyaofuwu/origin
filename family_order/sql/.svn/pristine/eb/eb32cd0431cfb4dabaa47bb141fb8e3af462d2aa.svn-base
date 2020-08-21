--IS_CACHE=Y
SELECT to_char(b.discnt_code) paracode, b.discnt_name paraname
FROM td_b_prod_discnt_member a, td_b_discnt b, td_b_product c
WHERE c.brand_code = 'VPGN' 
  AND a.product_id = c.product_id 
  AND b.discnt_code =a.discnt_code
  AND SYSDATE BETWEEN a.start_date AND a.end_date
  AND SYSDATE BETWEEN b.start_date AND b.end_date
  AND SYSDATE BETWEEN c.start_date AND c.end_date
  AND ( :TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL )