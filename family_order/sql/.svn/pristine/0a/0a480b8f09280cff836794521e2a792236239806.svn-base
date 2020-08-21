SELECT TO_CHAR(MAX(trade_id)) trade_id
FROM tf_bh_trade a
 WHERE user_id = TO_NUMBER(:USER_ID)
 AND trade_type_code IN (190,192,7230,7240)
 AND cancel_tag = '0'