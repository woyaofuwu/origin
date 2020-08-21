--IS_CACHE=Y
select b.order_type_code,b.trade_type_code,b.rule_biz_id 
 from td_bre_order_rule b 
 where 1 = 1 
  and b.order_type_code = :ORDER_TYPE_CODE
  and b.trade_type_code = :TRADE_TYPE_CODE
  and sysdate between b.start_date and b.end_date
  order by rule_biz_id 