SELECT COUNT(1) recordcount
  FROM tf_b_trade_svc a
 WHERE a.modify_tag = '1'
   AND a.user_id = :USER_ID
   AND a.trade_id = :TRADE_ID
   AND a.service_id IN (SELECT d.service_id
                          FROM tf_b_trade         b,
                               tf_b_trade_product c,
                               td_b_product_svc   d
                         WHERE d.force_tag = '1'
                           AND c.product_id = d.product_id
                           AND b.trade_id = c.trade_id
                           AND b.user_id = :USER_ID
                           AND b.exec_time > SYSDATE)