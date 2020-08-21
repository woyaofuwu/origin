SELECT to_char(trade_id) trade_id,to_char(batch_id) batch_id,
	trade_type_code,eparchy_code,cust_name,serial_number,
	invoice_no,in_mode_code,product_id,
	to_char(oper_fee) oper_fee,to_char(foregift) foregift,
	to_char(advance_pay) advance_pay,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,
	accept_month,trade_staff_id,
	to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,
	to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,
	remark,rsrv_str2,
   	rsrv_str3,rsrv_str4,rsrv_str5,
   	rsrv_str6,rsrv_str7,rsrv_str8,
   	rsrv_str9,rsrv_str10
  FROM tf_bh_trade
  WHERE trade_type_code=:TRADE_TYPE_CODE
  AND trade_id IN (
    SELECT trade_id FROM tf_bh_trade WHERE user_id=TO_NUMBER(:USER_ID) AND trade_type_code=:TRADE_TYPE_CODE
    UNION ALL
    SELECT trade_id FROM tf_b_trade_relation WHERE USER_ID_B=TO_NUMBER(:USER_ID))
  AND (to_char(accept_date,'YYYY-MM-DD') >=:START_DATE)
     and (to_char(accept_date,'YYYY-MM-DD') <= :FINISH_DATE)
  AND NOT EXISTS (SELECT 1 FROM td_s_commpara b WHERE b.subsys_code='CSM'
  AND b.param_attr=7800 AND trade_staff_id = b.para_code1 AND trade_type_code=TO_NUMBER(b.para_code2)
  AND b.end_date>SYSDATE)
UNION ALL
SELECT to_char(trade_id) trade_id,to_char(batch_id) batch_id,
	trade_type_code,eparchy_code,cust_name,serial_number,
	invoice_no,in_mode_code,product_id,
	to_char(oper_fee) oper_fee,to_char(foregift) foregift,
	to_char(advance_pay) advance_pay,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,
	accept_month,trade_staff_id,
	to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,
	to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,
	remark,rsrv_str2,
   	rsrv_str3,rsrv_str4,rsrv_str5,
   	rsrv_str6,rsrv_str7,rsrv_str8,
   	rsrv_str9,rsrv_str10
  FROM tf_b_trade
  WHERE trade_type_code=:TRADE_TYPE_CODE
  AND trade_id IN (
    SELECT trade_id FROM tf_b_trade WHERE user_id=TO_NUMBER(:USER_ID) AND trade_type_code=:TRADE_TYPE_CODE
    UNION ALL
    SELECT trade_id FROM tf_b_trade_relation WHERE USER_ID_B=TO_NUMBER(:USER_ID))
  AND (to_char(accept_date,'YYYY-MM-DD') >=:START_DATE)
   	and (to_char(accept_date,'YYYY-MM-DD') <= :FINISH_DATE)
  AND NOT EXISTS (SELECT 1 FROM td_s_commpara b WHERE b.subsys_code='CSM'
  AND b.param_attr=7800 AND trade_staff_id = b.para_code1 AND trade_type_code=TO_NUMBER(b.para_code2)
  AND b.end_date>SYSDATE)