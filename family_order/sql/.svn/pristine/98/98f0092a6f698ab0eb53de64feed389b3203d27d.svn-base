insert into tf_b_trade(trade_id,order_id,bpm_id,trade_type_code,in_mode_code,priority,subscribe_type,subscribe_state,
  next_deal_tag,product_id,brand_code,user_id,cust_id,acct_id,serial_number,net_type_code,cust_name,accept_date,
  accept_month,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,
  term_ip,eparchy_code,city_code,olcom_tag,exec_time,finish_date,oper_fee,foregift,advance_pay,
  invoice_no,fee_state,fee_time,fee_staff_id,cancel_tag,cancel_date,cancel_staff_id,cancel_depart_id,
  cancel_city_code,cancel_eparchy_code,process_tag_set,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,
  rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,remark)
select to_number(:INTRADE_ID),to_number(:INTRADE_ID),null,14,'0',320,'0','0',
  '0',product_id,brand_code,user_id,cust_id,acct_id,serial_number,'00',:CUST_NAME,sysdate,
  to_number(to_char(sysdate,'mm')),:TRADE_STAFF_ID,:TRADE_DEPART_ID,:TRADE_CITY_CODE,:TRADE_EPARCHY_CODE,
  :TERM_IP,eparchy_code,city_code,'0',sysdate,null,to_number(nvl(:DEVICE_PRICE,'0')),0,to_number(nvl(:ADVANCE_PAY,'0')),
  null,'1',sysdate,:TRADE_STAFF_ID,'0',null,null,null,
  null,null,:PROCESS_TAG_SET,:RSRV_STR1,:RSRV_STR2,:RSRV_STR3,:RSRV_STR4,
  :RSRV_STR5,:RSRV_STR6,:RSRV_STR7,:RSRV_STR8,:RSRV_STR9,:RSRV_STR10,:REMARK
from tf_bh_trade
where trade_id=to_number(:TRADE_ID)