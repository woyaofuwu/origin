select to_char(a.trade_id) trade_id,
       b.accept_month,
       a.receipt_info1,
       a.receipt_info2,
       a.receipt_info3,
       a.receipt_info4,
       a.receipt_info5,
       a.notice_content,
       trade_type_code priority,
       b.trade_staff_id staff_id,
       b.trade_depart_id depart_id,
       to_char(b.accept_date, 'yyyy-mm-dd hh24:mi:ss') accept_date,
       substr(process_tag_set, 20, 2) check_mode,
       cust_name
  from TF_B_TRADE_CNOTE_INFO a, tf_b_trade b
 where a.trade_id = b.trade_id
   and a.trade_id = :TRADE_ID
union all
select to_char(a.trade_id) trade_id,
       b.accept_month,
       a.receipt_info1,
       a.receipt_info2,
       a.receipt_info3,
       a.receipt_info4,
       a.receipt_info5,
       a.notice_content,
       trade_type_code priority,
       b.trade_staff_id staff_id,
       b.trade_depart_id depart_id,
       to_char(b.accept_date, 'yyyy-mm-dd hh24:mi:ss') accept_date,
       substr(process_tag_set, 20, 2) check_mode,
       b.cust_name
  from TF_B_TRADE_CNOTE_INFO a, tf_bh_trade b
 where a.trade_id = b.trade_id
   and a.trade_id = :TRADE_ID
