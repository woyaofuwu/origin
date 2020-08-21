SELECT COUNT(1) recordcount
  FROM tf_b_trade
 WHERE product_id = :PRODUCT_ID
   AND cust_id = TO_NUMBER(:CUST_ID)
   AND rownum < 2