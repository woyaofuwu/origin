SELECT  t.serial_number,s.rule_id,s.score_changed,
to_char(t.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date, t.trade_staff_id,t.trade_depart_id,t.trade_type_code,
 s.action_count,'' cancel_tag,'' cancel_staff_id,'' cancel_date
  FROM tf_bh_trade t,tf_b_trade_score s
 WHERE s.trade_id = t.trade_id
   AND s.accept_month = t.accept_month
   and s.accept_month = to_number(substr(:START_DATE, 6, 2))
   and t.accept_month = to_number(substr(:START_DATE, 6, 2))
   AND t.cancel_tag = '0' 
   AND t.accept_date >= to_date(:START_DATE,'yyyy-mm-dd')
   AND t.accept_date <= to_date(:END_DATE,'yyyy-mm-dd')+1 
   AND t.trade_staff_id >= TRIM(:TRADE_STAFF_ID_S)
   AND t.trade_staff_id <= TRIM(:TRADE_STAFF_ID_E)
   AND t.trade_depart_id = TRIM(:TRADE_DEPART_ID)