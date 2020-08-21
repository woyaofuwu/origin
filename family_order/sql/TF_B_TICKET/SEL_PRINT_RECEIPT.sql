select t.trade_id,
       t.fee_mode,
       t.serial_number,
       t.fee,
       to_char(t.accept_time,'yyyy-mm-dd hh24:mi:ss') as accept_time,
       t.accept_month,
       t.ticket_id,
       t.tax_no,
       t.trade_type_code
  from tf_b_ticket t
 where 1 = 1
   and t.trade_id = :TRADE_ID
   and t.partition_id = to_number(substr(:TRADE_ID,length(:TRADE_ID)-3))
   and t.serial_number = :SERIAL_NUMBER
   and t.ticket_type_code in ('D','E')
   and t.ticket_state_code = '0'