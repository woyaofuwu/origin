INSERT INTO tf_b_trade_mbmp(trade_id,user_id,serial_number,biz_type_code,org_domain,opr_source,passwd,biz_state_code,
start_date,end_date,modify_tag,trade_staff_id,trade_depart_id,trade_time)
SELECT trade_id,user_id,serial_number,decode(decode(trade_type_code,'1025',RSRV_STR6,'1029',RSRV_STR6,RSRV_STR2),decode(decode(trade_type_code,'1025',RSRV_STR5,'1029',RSRV_STR5,RSRV_STR3),'25','99','EWQSE'),'98',decode(trade_type_code,'1025',RSRV_STR5,'1029',RSRV_STR5,RSRV_STR3)),BRAND_CODE,decode(trade_type_code,'1025',RSRV_STR7,'1029',RSRV_STR7,RSRV_STR1),RSRV_STR4,
decode(decode(trade_type_code,'1025',RSRV_STR6,'1029',RSRV_STR6,RSRV_STR2),DECODE(decode(trade_type_code,'1025',RSRV_STR5,'1029',RSRV_STR5,RSRV_STR3),'98','12','25','99','02'),'E','A'),to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),
to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),decode(decode(trade_type_code,'1025',RSRV_STR6,'1029',RSRV_STR6,RSRV_STR2),DECODE(decode(trade_type_code,'1025',RSRV_STR5,'1029',RSRV_STR5,RSRV_STR3),'98','11','01'),'0',
DECODE(decode(trade_type_code,'1025',RSRV_STR5,'1029',RSRV_STR5,RSRV_STR3),'98','12','25','99','02'),'1','2'),trade_staff_id,trade_depart_id,accept_date
FROM tf_b_trade
WHERE trade_id = to_number(:TRADE_ID)