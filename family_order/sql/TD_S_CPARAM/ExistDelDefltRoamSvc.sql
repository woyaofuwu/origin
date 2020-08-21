SELECT COUNT(1) recordcount
  FROM td_b_product_svc a
 WHERE product_id=:PRODUCT_ID
   AND default_tag='1'
   AND SYSDATE BETWEEN start_date  AND end_date
   AND NOT EXISTS(SELECT 1 FROM tf_b_trade_svc
                   WHERE trade_id=:TRADE_ID
                     AND accept_month=:ACCEPT_MONTH
                     AND user_id=:USER_ID
                     AND modify_tag='0'
                     AND service_id=a.service_id
                     )
   AND service_id IN (13,14,15,16,17,18,19,100,101)