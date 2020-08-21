SELECT NVL(max(to_number(substr(SUBSCRIBE_ID,11,4))),0) recordcount
  FROM tf_b_trade_iagw
 WHERE serial_number = :SERIAL_NUMBER
  and in_mode = :IN_MODE
  and user_id = :USER_ID