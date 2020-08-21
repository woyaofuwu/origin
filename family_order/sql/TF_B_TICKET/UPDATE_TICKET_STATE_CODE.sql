update tf_b_ticket t
set t.TICKET_STATE_CODE = :OPER_TYPE
where 1=1
and t.trade_id = :TRADE_ID
and t.partition_id = to_number(substr(:TRADE_ID,length(:TRADE_ID)-3))
and t.ticket_state_code = :TICKET_STATE_CODE
and t.ticket_type_code = :TICKET_TYPE_CODE
and t.fee_mode = :FEE_MODE