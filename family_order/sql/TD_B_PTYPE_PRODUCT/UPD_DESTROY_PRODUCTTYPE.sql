UPDATE td_b_ptype_product
 SET end_date = sysdate  
WHERE product_id = :PRODUCT_ID AND end_date > SYSDATE