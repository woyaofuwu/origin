SELECT to_char(trade_id) trade_id,to_char(order_id) order_id,to_char(bpm_id) bpm_id,trade_type_code,in_mode_code,priority,subscribe_state,next_deal_tag,product_id,brand_code,to_char(user_id) user_id,to_char(cust_id) cust_id,to_char(acct_id) acct_id,serial_number,cust_name,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,accept_month,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,term_ip,eparchy_code,city_code,olcom_tag,to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,to_char(oper_fee) oper_fee,to_char(foregift) foregift,to_char(advance_pay) advance_pay,invoice_no,fee_state,to_char(fee_time,'yyyy-mm-dd hh24:mi:ss') fee_time,fee_staff_id,cancel_tag,to_char(cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,cancel_staff_id,cancel_depart_id,cancel_city_code,cancel_eparchy_code,process_tag_set,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,remark
  FROM tf_b_trade
 WHERE (trade_id,accept_month) =
    (SELECT max(a.trade_id),to_number(substr(to_char(max(a.trade_id)),5,2))
       FROM tf_b_trade a, td_s_tradetype b
      WHERE a.serial_number = :SERIAL_NUMBER
        AND a.accept_date+0 >= TO_DATE(:BEGIN_DATE, 'YYYY-MM-DD HH24:MI:SS')
        AND a.accept_date+0 <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
        AND a.cancel_tag = '0'
        AND a.trade_type_code = :TRADE_TYPE_CODE
        AND b.trade_type_code = a.trade_type_code
        AND b.back_tag = '1'
        AND b.eparchy_code = a.eparchy_code
        AND SYSDATE BETWEEN b.start_date AND b.end_date)