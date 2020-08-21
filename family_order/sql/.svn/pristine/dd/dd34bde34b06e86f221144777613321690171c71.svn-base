select t.print_id,t.ticket_id,t.tax_no,t.fee_mode,t.security_code,t.trade_staff_id,t.fee,
	 to_char(t.accept_time,'yyyy-mm-dd hh24:mi:ss') as accept_time
  from tf_b_ticket t
 where 1 = 1
   and t.trade_id = :TRADE_ID
   and t.partition_id = to_number(substr(:TRADE_ID,length(:TRADE_ID)-3))
   and t.ticket_type_code in ('D','E')
   and t.security_code is not null
   and t.ticket_state_code = '0'
   and t.rsrv_str1 is null
