SELECT to_char(trade_id) trade_id,to_char(subscribe_id) subscribe_id,to_char(bpm_id) bpm_id,trade_type_code,in_mode_code,priority,
       subscribe_state,next_deal_tag,product_id,brand_code,to_char(a.user_id) user_id,to_char(cust_id) cust_id,to_char(acct_id) acct_id,
       serial_number,cust_name,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,accept_month,trade_staff_id,trade_depart_id,
       trade_city_code,trade_eparchy_code,term_ip,eparchy_code,city_code,olcom_tag,to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,
       to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,to_char(oper_fee) oper_fee,to_char(foregift) foregift,
       to_char(advance_pay) advance_pay,invoice_no,fee_state,to_char(fee_time,'yyyy-mm-dd hh24:mi:ss') fee_time,fee_staff_id,cancel_tag,
       to_char(cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,cancel_staff_id,cancel_depart_id,cancel_city_code,cancel_eparchy_code,
       process_tag_set,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,remark 
  FROM tf_bh_trade a,tf_f_user_other b
 WHERE b.partition_id = mod(to_number(:USER_ID),10000)
   and b.user_id = to_number(:USER_ID)
   and b.rsrv_value_code = :RSRV_VALUE_CODE
   and b.rsrv_value = :RSRV_VALUE
   and a.trade_id = b.rsrv_str1
   and a.accept_month = to_number(b.rsrv_str2)