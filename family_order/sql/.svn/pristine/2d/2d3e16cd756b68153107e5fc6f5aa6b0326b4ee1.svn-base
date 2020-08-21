SELECT COUNT(1) recordcount
  FROM tf_f_user u, tf_f_user_product p
 WHERE u.user_id = p.user_id 
   AND u.CUST_ID=:CUST_ID
   AND u.REMOVE_TAG='0'
   AND p.PRODUCT_ID >= 9901 AND p.PRODUCT_ID <= 9903
   AND ROWNUM < 2 