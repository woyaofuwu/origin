--IS_CACHE=Y
SELECT COUNT(*) recordcount
  FROM td_b_product a,td_b_product_relation b,td_b_product_release c
 WHERE b.relation_type_code = :RELATION_TYPE_CODE
   AND b.product_id = a.product_id
   AND a.brand_code = :BRAND_CODE
   AND a.product_id = c.product_id
   AND c.release_eparchy_code = :EPARCHY_CODE
   AND SYSDATE BETWEEN c.start_date AND c.end_date