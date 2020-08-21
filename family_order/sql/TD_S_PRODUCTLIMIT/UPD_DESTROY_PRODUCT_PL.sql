UPDATE td_s_productlimit
SET end_date=sysdate 
WHERE PRODUCT_ID_A = :PRODUCT_ID_A
AND PRODUCT_ID_B = :PRODUCT_ID_B