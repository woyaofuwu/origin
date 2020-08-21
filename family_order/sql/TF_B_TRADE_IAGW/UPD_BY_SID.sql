UPDATE tf_b_trade_iagw
   SET err_code=:ERR_CODE,deal_tag=:DEAL_TAG  
 WHERE serial_number=:SERIAL_NUMBER
   AND subscribe_id=:SUBSCRIBE_ID