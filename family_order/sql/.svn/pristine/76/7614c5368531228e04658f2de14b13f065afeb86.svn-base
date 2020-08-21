SELECT a.trade_id,a.serial_number,a.user_id FROM tf_b_trade a, tf_b_trade_sale_goods b
WHERE a.trade_id=b.trade_id
AND a.accept_month=b.accept_month
AND a.user_id=b.user_id
AND a.trade_type_code=240
AND a.serial_number=:SERIAL_NUMBER
AND b.modify_tag='0'
AND b.res_code=:RES_CODE