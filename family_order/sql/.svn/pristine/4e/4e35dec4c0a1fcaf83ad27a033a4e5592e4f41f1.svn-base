SELECT DECODE(SIGN(COUNT(1)),1,0,1) recordcount
FROM tf_b_trade_product a
WHERE EXISTS (
SELECT 1
FROM tf_bh_trade
WHERE trade_type_code IN (10,11) AND user_id=:USER_ID
AND a.trade_id=trade_id AND a.accept_month=accept_month AND cancel_tag='0')
AND product_id LIKE '%880015' AND modify_tag IN ('0','2')