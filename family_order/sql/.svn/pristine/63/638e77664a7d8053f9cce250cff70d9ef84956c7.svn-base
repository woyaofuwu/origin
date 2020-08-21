SELECT  count(1) recordcount
FROM
(
SELECT 1
  FROM tf_f_user
 WHERE product_id=to_number(:PRODUCT_ID)
   AND user_id=to_number(:USER_ID)
UNION all
SELECT 1
  FROM tf_b_trade
   where trade_type_code=110
   AND user_id=to_number(:USER_ID)
   AND product_id=to_number(:PRODUCT_ID)
   AND exec_time>SYSDATE)