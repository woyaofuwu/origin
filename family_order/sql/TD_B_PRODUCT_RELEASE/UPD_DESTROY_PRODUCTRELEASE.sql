UPDATE td_b_product_release
   SET end_date = sysdate  
WHERE product_id = :PRODUCT_ID
AND start_date = TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')
AND (release_eparchy_code = :RELEASE_EPARCHY_CODE)