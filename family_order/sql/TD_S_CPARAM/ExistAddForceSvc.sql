SELECT COUNT(1) recordcount FROM td_b_product_svc c
WHERE c.product_id=:PRODUCT_ID
AND c.force_tag='1' AND c.end_date>SYSDATE
AND NOT EXISTS (SELECT 1 FROM tf_f_user_svc b WHERE b.service_id=c.service_id
AND b.user_id=:USER_ID AND b.end_date>SYSDATE)
AND NOT EXISTS (SELECT 1 FROM tf_b_trade_svc a WHERE a.trade_id = :TRADE_ID
AND a.modify_tag = '0' AND a.service_id=c.service_id)