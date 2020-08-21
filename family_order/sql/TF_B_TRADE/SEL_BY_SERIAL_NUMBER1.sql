select b.trade_id,b.order_id,b.trade_type_code,b.accept_date,b.exec_time,b.trade_staff_id,
b.trade_depart_id,b.serial_number from tf_b_trade b
 where b.serial_number=:SERIAL_NUMBER
and b.cancel_tag='0'
and b.trade_type_code='124'
and mod(b.subscribe_type,10)=1
and b.exec_time>sysdate