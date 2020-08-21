SELECT to_char(a.trade_id) trade_id,
       to_char(a.subscribe_id) subscribe_id,
       to_char(a.bpm_id) bpm_id,
       a.trade_type_code,
       a.in_mode_code,
       a.priority,
       a.subscribe_state,
       a.next_deal_tag,
       a.product_id,
       a.brand_code,
       to_char(a.user_id) user_id,
       to_char(a.cust_id) cust_id,
       to_char(a.acct_id) acct_id,
       a.serial_number,
       a.cust_name,
       to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,
       a.accept_month,
       a.trade_staff_id,
       a.trade_depart_id,
       a.trade_city_code,
       a.trade_eparchy_code,
       a.term_ip,
       a.eparchy_code,
       a.city_code,
       a.olcom_tag,
       to_char(a.exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,
       to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,
       to_char(a.oper_fee) oper_fee,
       to_char(a.foregift) foregift,
       to_char(a.advance_pay) advance_pay,
       a.invoice_no,
       a.fee_state,
       to_char(a.fee_time,'yyyy-mm-dd hh24:mi:ss') fee_time,
       a.fee_staff_id,
       a.cancel_tag,
       b.rsrv_str1 cancel_date,
       b.rsrv_str2 cancel_staff_id,
       a.cancel_depart_id,
       a.cancel_city_code,
       a.cancel_eparchy_code,
       a.process_tag_set,
       e.score_changed rsrv_str1,
       a.rsrv_str2,
       decode(b.rsrv_value_code,'SI','1','0') rsrv_str3,
       b.rsrv_str3 rsrv_str4,
       a.rsrv_str5,
       a.rsrv_str6,
       a.rsrv_str7,
       a.rsrv_str8,
       a.rsrv_str9,
       a.rsrv_str10,
       a.remark
  FROM tf_bh_trade a,tf_f_user_other b,tf_b_trade_score e
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.cancel_tag = '0'
   AND a.trade_type_code=330
   AND b.user_id(+) = a.user_id
   AND b.rsrv_value_code(+) = 'SI'
   AND b.rsrv_value(+) = a.trade_id
   AND a.trade_id=e.trade_id
   AND EXISTS(SELECT 1
                FROM tf_b_trade_scoresub c,td_b_score_action d
               WHERE c.action_code=d.action_code
                 AND d.exchange_type_code='9'
                 AND c.trade_id=a.trade_id)