--IS_CACHE=Y
SELECT b.service_id, b.res_type_code
  FROM td_b_product_svc a, td_s_service_res b
 WHERE a.product_id = :PRODUCT_ID
   AND b.service_id = a.service_id
   AND sysdate BETWEEN a.start_date AND a.end_date
   AND sysdate BETWEEN b.start_date AND b.end_date