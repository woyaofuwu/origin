select t.print_id, 
			 t.trade_id,
       t.fee_mode,
       t.serial_number,
       t.security_code as fwm,
	     t.fee,
       to_char(t.accept_time,'yyyy-mm-dd hh24:mi:ss') as accept_time,
       t.accept_month,
       t.ticket_id,
       t.tax_no,
       t.trade_type_code,
       t.trade_eparchy_code,
       t.trade_staff_id,
       t.trade_depart_id
  from tf_b_ticket t
 where 1 = 1
   and t.trade_id = :TRADE_ID
   and t.partition_id = to_number(substr(:TRADE_ID,length(:TRADE_ID)-3))
   and t.serial_number = :SERIAL_NUMBER
   and t.trade_staff_id = :TRADE_STAFF_ID
   and t.accept_month = to_number(:ACCEPT_MONTH)
   and to_char(t.accept_time,'yyyy-mm') = :ACCEPT_TIME
   and t.ticket_id = :TICKET_ID
   and t.ticket_id is not null
   and t.tax_no is not null
   and t.rsrv_str1 is null
   and t.security_code is not null
   and t.ticket_type_code in ('D','E')
   and t.ticket_state_code = '0'
   and to_char(sysdate,'yyyymm') != to_char(t.accept_time,'yyyymm')