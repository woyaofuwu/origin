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
       b.accept_date brand,
       substr(process_tag_set, 20, 1) vip_class,
       cust_name
  from TF_B_TRADE_CNOTE_INFO a, tf_b_trade b
 where a.trade_id = b.trade_id
   and a.accept_month = b.accept_month 
   and b.trade_id = :TRADE_ID   
   and b.serial_number = :SERIAL_NUMBER
   and b.accept_date+0 > TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   and b.accept_date+0 <  TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   and (b.trade_staff_id = :TRADE_STAFF_ID or :TRADE_STAFF_ID is null)
   and b.cancel_tag = :CANCEL_TAG
union all
select  
       to_char(a.trade_id) trade_id,
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
       b.accept_date brand,   
       substr(process_tag_set, 20, 1) vip_class,
       cust_name
  from TF_B_TRADE_CNOTE_INFO a, tf_bh_trade b
 where a.trade_id = b.trade_id
   and a.accept_month = b.accept_month
   and b.trade_id = :TRADE_ID
   and b.serial_number = :SERIAL_NUMBER
   and b.accept_date+0 > TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   and b.accept_date+0 < TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   and (b.trade_staff_id = :TRADE_STAFF_ID or :TRADE_STAFF_ID is null)
   and b.cancel_tag = :CANCEL_TAG