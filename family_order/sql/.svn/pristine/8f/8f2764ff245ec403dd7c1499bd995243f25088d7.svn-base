select b.trade_id,
       b.accept_month,
       b.trade_type_code ,   
       b.trade_staff_id staff_id,
       b.trade_depart_id depart_id,
       b.accept_date ,   
       substr(process_tag_set, 20, 1) vip_class,
       cust_name,b.serial_number
  from tf_bh_trade b
 where b.batch_id is null and b.trade_type_code in (6113,6111,6110,6114,6117,6115,3647,3644,3039,3034,2711,3035,3037,3657,2710)
   and b.serial_number = :SERIAL_NUMBER
   and b.accept_date+0 > TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   and b.accept_date+0 < TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   and (b.trade_staff_id = :TRADE_STAFF_ID or :TRADE_STAFF_ID is null)
   and b.cancel_tag = :CANCEL_TAG
   and not exists 
   ( select * from tf_b_trade_cnote_info a 
   where a.trade_id = b.trade_id and a.accept_month = b.accept_month and a.note_type='3')