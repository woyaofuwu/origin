select to_char(a.trade_id) trade_id, a.accept_month, to_char(a.user_id) user_id, inst_type, to_char(a.inst_id) inst_id,
       a.attr_code, a.attr_value, decode(a.modify_tag, '4','0','5','1', a.modify_tag) modify_tag
 from tf_b_trade_svc s, tf_b_trade_attr a
 where a.inst_type = 'S'  
  and a.inst_id = s.inst_id
  and a.attr_code = :ATTR_CODE
  and s.service_id = to_number(:SERVICE_ID) 
  and a.trade_id = s.trade_id 
  and a.accept_month = s.accept_month 
  and a.end_date >= to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
  and s.trade_id = TO_NUMBER(:TRADE_ID) 
  and s.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))