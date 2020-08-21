SELECT TO_CHAR(USER_ID) USER_ID,
       CUST_NAME,
       PHONE,
       PORT_OUT_NETID
  FROM tf_b_trade_np
 WHERE serial_number = :SERIAL_NUMBER
 AND trade_type_code = 40
 AND trade_id in (select max(t.trade_id) from tf_b_trade_np t where t.serial_number = :SERIAL_NUMBER and t.trade_type_code = 40)