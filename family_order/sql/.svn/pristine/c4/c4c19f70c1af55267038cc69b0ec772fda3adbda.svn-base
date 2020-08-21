select to_char(a.trade_id) trade_id,a.accept_month,a.receipt_info1,a.receipt_info2,a.receipt_info3,
       a.receipt_info4,a.receipt_info5,a.notice_content,
       trade_type_code priority,trade_staff_id staff_id,trade_depart_id depart_id,
       accept_date brand,substr(process_tag_set,20,1) vip_class,cust_name
from tf_b_tradeinfo a,tf_b_trade b
where a.trade_id=b.trade_id
  and a.accept_month=b.accept_month
  and b.serial_number=:SERIAL_NUMBER
  and b.accept_date>TRUNC(TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'))
  and b.accept_date<TRUNC(TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')+1)
  and (b.trade_staff_id=:TRADE_STAFF_ID or :TRADE_STAFF_ID is null)
  and b.cancel_tag=:CANCEL_TAG
union
select to_char(a.trade_id) trade_id,a.accept_month,a.receipt_info1,a.receipt_info2,a.receipt_info3,
       a.receipt_info4,a.receipt_info5,a.notice_content,
       trade_type_code priority,trade_staff_id staff_id,trade_depart_id depart_id,
       accept_date brand,substr(process_tag_set,20,1) vip_class,cust_name
from tf_b_tradeinfo a,tf_bh_trade b
where a.trade_id=b.trade_id
  and a.accept_month=b.accept_month
  and b.serial_number=:SERIAL_NUMBER
  and b.accept_date>TRUNC(TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'))
  and b.accept_date<TRUNC(TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')+1)
  and (b.trade_staff_id=:TRADE_STAFF_ID or :TRADE_STAFF_ID is null)
  and b.cancel_tag=:CANCEL_TAG