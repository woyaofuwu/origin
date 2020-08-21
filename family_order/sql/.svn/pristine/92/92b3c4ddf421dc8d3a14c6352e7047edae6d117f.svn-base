SELECT /*+ leading(t) use_nl(r,t,s)*/ t.serial_number,s.rule_id,r.rule_name,s.score_changed,
to_char(t.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date, t.trade_staff_id,t.trade_depart_id,t.trade_type_code,
 s.action_count,
 decode(t.cancel_tag,'0','正常','1','被返销','2','返销') cancel_tag,
 decode(t.cancel_tag,'0','','1','','2',cancel_staff_id) cancel_staff_id,
 decode(t.cancel_tag,'0','','1','','2',to_char(cancel_date,'yyyy-mm-dd hh24:mi:ss'))  cancel_date
  FROM td_b_exchange_rule r, tf_bh_trade t,tf_b_trade_score s
 WHERE s.trade_id = t.trade_id
   AND s.accept_month = t.accept_month
   and s.accept_month = to_number(substr(:START_DATE, 6, 2))
   and t.accept_month = to_number(substr(:START_DATE, 6, 2))
   AND s.rule_id = r.rule_id
   AND t.accept_date >= to_date(:START_DATE,'yyyy-mm-dd')
   AND t.accept_date <= to_date(:END_DATE,'yyyy-mm-dd')+1 
   AND t.trade_staff_id >= TRIM(:TRADE_STAFF_ID_S)
   AND t.trade_staff_id <= TRIM(:TRADE_STAFF_ID_E)
   AND (t.trade_depart_id = TRIM(:TRADE_DEPART_ID) or :TRADE_DEPART_ID is null)
   ORDER BY accept_date