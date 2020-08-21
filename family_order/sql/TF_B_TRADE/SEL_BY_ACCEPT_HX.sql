SELECT to_char(trade_id) trade_id,to_char(order_id) order_id,to_char(bpm_id) bpm_id,trade_type_code,in_mode_code,priority,subscribe_state,next_deal_tag,product_id,brand_code,to_char(user_id) user_id,to_char(cust_id) cust_id,to_char(acct_id) acct_id,serial_number,cust_name,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,accept_month,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,term_ip,eparchy_code,city_code,olcom_tag,to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,to_char(oper_fee) oper_fee,to_char(foregift) foregift,to_char(advance_pay) advance_pay,invoice_no,fee_state,to_char(fee_time,'yyyy-mm-dd hh24:mi:ss') fee_time,fee_staff_id,cancel_tag,to_char(cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,cancel_staff_id,cancel_depart_id,cancel_city_code,cancel_eparchy_code,process_tag_set,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,remark
FROM tf_b_trade
WHERE (trade_type_code=:TRADE_TYPE_CODE OR TO_NUMBER(:TRADE_TYPE_CODE)=0)
and (serial_number=:SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
and accept_date >= TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
and (accept_date <TRUNC(TO_DATE(:FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS'))+1 OR :FINISH_DATE IS NULL)
and (trade_depart_id=:TRADE_DEPART_ID OR :TRADE_DEPART_ID IS NULL)
and (trade_staff_id=:TRADE_STAFF_ID OR :TRADE_STAFF_ID IS NULL)
and (TRADE_EPARCHY_CODE=:TRADE_EPARCHY_CODE)
AND SUBSCRIBE_TYPE <> '600'
order by accept_date