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
       t.ticket_state_code,
       t.ticket_type_code
  from tf_b_ticket t
 where 1 = 1
   and t.print_id = :PRINT_ID