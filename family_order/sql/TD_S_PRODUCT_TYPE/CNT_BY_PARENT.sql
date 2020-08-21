--IS_CACHE=Y
SELECT count(*)
  FROM td_s_product_type
 WHERE parent_ptype_code=:PARENT_PTYPE_CODE
   AND sysdate BETWEEN start_date AND end_date
   ORDER BY 1