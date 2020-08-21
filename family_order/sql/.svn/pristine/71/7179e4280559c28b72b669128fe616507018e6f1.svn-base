SELECT  *
FROM tf_bh_trade a
 WHERE user_id = TO_NUMBER(:USER_ID)
 AND trade_type_code IN (190,192,7230,7240,3804)
 AND cancel_tag = '0'
 order BY trade_id desc