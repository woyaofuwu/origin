select s.* from tf_f_bank_recvfee s where substr(s.trade_time,0,8)  = TO_CHAR(TRUNC(SYSDATE),'YYYYMMDD')
and s.Bank_Tag Not In(4) and s.charge_id is null
union all
select s.* from tf_f_bank_recvfee s,tf_a_paylog b where   
s.charge_id = b.charge_id  and 
substr(s.trade_time,0,8)  = TO_CHAR(TRUNC(SYSDATE),'YYYYMMDD') 
and b.cancel_tag = 1 
and s.Cancel_Tag <>  '2' And s.Bank_Tag Not In ('6')