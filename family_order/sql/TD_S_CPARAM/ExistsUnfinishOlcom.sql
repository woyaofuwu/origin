SELECT COUNT(*) recordcount FROM tf_b_trade
 WHERE serial_number = :SERIAL_NUMBER
   AND trade_type_code = :TRADE_TYPE_CODE