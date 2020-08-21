SELECT COUNT(1) recordcount FROM dual WHERE
(SELECT SUM(fee) FROM tf_b_tradefee_sub a WHERE trade_id IN (
SELECT trade_id FROM tf_bh_trade WHERE user_id=:USER_ID
AND trunc(accept_date,'mm')=trunc(SYSDATE,'mm') AND trade_type_code=:TRADE_TYPE_CODE AND cancel_tag='0'
UNION ALL
SELECT trade_id FROM tf_b_trade WHERE user_id=:USER_ID
AND trunc(accept_date,'mm')=trunc(SYSDATE,'mm') AND trade_type_code=:TRADE_TYPE_CODE AND cancel_tag='0')
AND fee_mode='2')
>=:NUM