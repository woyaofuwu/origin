DELETE FROM td_b_ptype_product
WHERE product_id = :PRODUCT_ID
AND start_date = TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')
AND (product_type_code= :PRODUCT_TYPE_CODE)