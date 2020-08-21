SELECT /*+ ordereed use_nl(b,a)*/COUNT(1) recordcount FROM td_b_product_svc b
WHERE b.product_id=:PRODUCT_ID
AND b.force_tag='1' AND b.end_date>SYSDATE
AND EXISTS (SELECT 1 FROM tf_b_trade_svc a
WHERE a.trade_id = :TRADE_ID
AND a.accept_month = to_number(substr(:TRADE_ID,5,2))
AND decode(a.modify_tag, '4', '0','5','1', modify_tag) = :MODIFY_TAG AND a.service_id=b.service_id)