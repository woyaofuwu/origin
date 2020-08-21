--IS_CACHE=Y
SELECT COUNT(1) recordcount
  FROM td_b_product_svc
 WHERE product_id=:PRODUCT_ID
   AND service_id=:SERVICE_ID
   AND (main_tag=:MAIN_TAG OR :MAIN_TAG='Z')
   AND (default_tag=:DEFAULT_TAG OR :DEFAULT_TAG='Z')
   AND (force_tag=:FORCE_TAG OR :FORCE_TAG='Z')