INSERT INTO tf_b_trade_mbmp(trade_id,user_id,serial_number,biz_type_code,org_domain,opr_source,passwd,biz_state_code,start_date,end_date,modify_tag,trade_staff_id,trade_depart_id,trade_time)
SELECT trade_id,user_id,serial_number,:BIZ_TYPE_CODE,:ORG_DOMAIN,decode(in_mode_code,'6','07','08'),'PASS',
decode(:MODIFY_TAG,'0','A','E'),
to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),
decode(:MODIFY_TAG,'0','0','2'),
trade_staff_id,trade_depart_id,accept_date
FROM tf_b_trade
WHERE trade_id = to_number(:TRADE_ID)