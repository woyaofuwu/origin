select 1
  from tf_b_ticket t
 where 1 = 1
   and t.trade_id = :TRADE_ID
   and t.partition_id = to_number(substr(:TRADE_ID,length(:TRADE_ID)-3))
   and t.ticket_type_code in ('D','E')
   and t.security_code is not null
   and t.fee < 0
   and t.ticket_state_code in ('4','6')
   and t.rsrv_str1 is null
