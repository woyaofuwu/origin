SELECT COUNT(1) recordcount
  FROM dual
 WHERE (SELECT service_id FROM td_b_product_svc
 WHERE product_id=:OLD_PRODUCT_ID AND service_id IN (16,17,18,19) AND default_tag='1') >
      (SELECT service_id FROM td_b_product_svc
 WHERE product_id=:NEW_PRODUCT_ID AND service_id IN (16,17,18,19) AND default_tag='1')