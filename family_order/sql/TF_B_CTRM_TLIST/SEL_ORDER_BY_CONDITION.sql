SELECT t.TID,
       t.OUT_TID,
       t.CHANNEL_ID,
       t.CREATE_TIME,
       t.PAY_TIME,
       decode(t.DISTRIBUTION,1,'需要',2,'不需要') DISTRIBUTION,
       t.BUYER_NICK,
       t.BUYER_EMAIL,
       t.PAYMENT,
       t.TOTAL_FEE,
       t.RECEIVER_ZIP,
       t.RECEIVER_NAME,
       t.RECEIVER_STATE,
       t.RECEIVER_CITY,
       t.RECEIVER_DISTRICT,
       t.RECEIVER_ADDRESS,
       t.RECEIVER_MOBILE,
       t.RECEIVER_PHONE,
       t.BUYER_MESSAGE,
       t.TRADE_MEMO,
       t.SELLER_MEMO,
       t.INVOICE_NAME,
       t.OPR_NUMB,
       t.STATUS,
       t.UPDATE_TIME,
       t.UPDATE_STAFF_ID,
       t.UPDATE_DEPART_ID,
       t.REMARK
  FROM TF_B_CTRM_TLIST t
 WHERE (t.tid = :TID or :TID is null)
   and t.create_time between to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') and
       to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')
   and exists (select 1 from TF_B_CTRM_ORDER o
       where t.tid = o.tid
       and (o.phone = :SERIAL_NUMBER OR :SERIAL_NUMBER is null))