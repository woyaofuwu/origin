select o.rsrv_value from tf_b_trade_svc t,tf_b_trade_other o
 where t.service_id='952701' 
  and t.user_id=o.user_id
  and t.inst_id=o.rsrv_str2
  and o.rsrv_value_code='MOA'
  and t.trade_id = :TRADE_ID
  and t.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  and o.trade_id = :TRADE_ID
  and o.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))