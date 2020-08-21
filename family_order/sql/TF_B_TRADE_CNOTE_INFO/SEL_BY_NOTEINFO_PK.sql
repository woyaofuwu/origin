select to_char(a.trade_id) trade_id,
       a.accept_month,
       a.receipt_info1,
       a.receipt_info2,
       a.receipt_info3,
       a.receipt_info4,
       a.receipt_info5,
       a.notice_content,
       a.trade_staff_id staff_id,
       a.trade_depart_id depart_id,
       a.accept_date brand
  from TF_B_TRADE_CNOTE_INFO a 
 where  a.trade_id = :TRADE_ID and
   a.ACCEPT_MONTH = :ACCEPT_MONTH and
   a.NOTE_TYPE = :NOTE_TYPE