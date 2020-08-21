select t.print_id,t.trade_id,t.ticket_id,t.tax_no,t.ticket_type_code,
t.security_code,t.trade_staff_id,t.serial_number,t.fee,t.trade_type_code,
t.fee_mode
  from tf_b_ticket t
 where 1 = 1
   and t.trade_id = :TRADE_ID
   and t.partition_id = to_number(substr(:TRADE_ID,length(:TRADE_ID)-3))
   and t.ticket_state_code = '0' ---
   and t.rsrv_str1 is null