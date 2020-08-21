SELECT to_char(a.trade_id) trade_id,'' order_id, '' bpm_id,trade_type_code,in_mode_code,priority,
  subscribe_state,next_deal_tag,product_id,brand_code,'' user_id,'' cust_id,'' acct_id,
  serial_number,cust_name,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,
  a.accept_month,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,term_ip,
  eparchy_code,city_code,olcom_tag,'' exec_time,'' finish_date,'' oper_fee,
  '' foregift,'' advance_pay,invoice_no,fee_state,'' fee_time,fee_staff_id,
  cancel_tag,to_char(cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,cancel_staff_id,
  cancel_depart_id,cancel_city_code,cancel_eparchy_code,process_tag_set,
  to_char(b.discnt_code) rsrv_str1,
  decode(b.modify_tag,'0','增加','1','删除','2','修改') rsrv_str2,
  to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss') rsrv_str3,
  to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') rsrv_str4,
  '' rsrv_str5, '' rsrv_str6, '' rsrv_str7, '' rsrv_str8, '' rsrv_str9, '' rsrv_str10,remark
from tf_b_trade a,tf_b_trade_discnt b
where a.trade_id=b.trade_id
  and a.accept_date>=to_date(:START_DATE,'yyyy-mm-dd')
  and a.accept_date<=to_date(:END_DATE,'yyyy-mm-dd')+1-0.00001
  and trim(a.trade_staff_id)>=:START_STAFF_ID
  and trim(a.trade_staff_id)<=:END_STAFF_ID
  and a.trade_city_code=:TRADE_CITY_CODE
  and (to_char(b.discnt_code)=:DISCNT_CODE OR :DISCNT_CODE is null)
  and a.cancel_tag!='2'
union all
SELECT to_char(a.trade_id) trade_id,'' order_id, '' bpm_id,trade_type_code,in_mode_code,priority,
  subscribe_state,next_deal_tag,product_id,brand_code,'' user_id,'' cust_id,'' acct_id,
  serial_number,cust_name,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,
  a.accept_month,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,term_ip,
  eparchy_code,city_code,olcom_tag,'' exec_time,'' finish_date,'' oper_fee,
  '' foregift,'' advance_pay,invoice_no,fee_state,'' fee_time,fee_staff_id,
  cancel_tag,to_char(cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,cancel_staff_id,
  cancel_depart_id,cancel_city_code,cancel_eparchy_code,process_tag_set,
  to_char(b.discnt_code) rsrv_str1,
  decode(b.modify_tag,'0','增加','1','删除','2','修改') rsrv_str2,
  to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss') rsrv_str3,
  to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') rsrv_str4,
  '' rsrv_str5, '' rsrv_str6, '' rsrv_str7, '' rsrv_str8, '' rsrv_str9, '' rsrv_str10,remark
from tf_bh_trade a,tf_b_trade_discnt b
where a.trade_id=b.trade_id
  and a.accept_date>=to_date(:START_DATE,'yyyy-mm-dd')
  and a.accept_date<=to_date(:END_DATE,'yyyy-mm-dd')+1-0.00001
  and trim(a.trade_staff_id)>=:START_STAFF_ID
  and trim(a.trade_staff_id)<=:END_STAFF_ID
  and a.trade_city_code=:TRADE_CITY_CODE
  and (to_char(b.discnt_code)=:DISCNT_CODE OR :DISCNT_CODE is null)
  and a.cancel_tag!='2'