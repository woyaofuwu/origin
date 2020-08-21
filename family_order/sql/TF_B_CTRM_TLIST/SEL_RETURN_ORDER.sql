SELECT r.REFUND_ID,
       r.CHANNEL_ID,
       r.REFUND_TYPE,
       r.OUT_REFUND_ID,
       r.TID,
       r.TRADE_MONEY,
       r. BUYER_NICK,
       r.REFUND_TIME,
       r.REFUND_STATUS,
       r.HAS_GOOD_RETURN,
       r.REFUND_MONEY,
       r.PAY_MONEY,
       r.REFUND_REASON,
       r.REFUND_DESCR,
       r.PRODUCT_TITLE,
       r.PRODUCT_PRICE,
       r.NUM,
       r.RETURN_BACK_TIIME,
       r.SELLER_REMARK,
       r.UPDATE_DEPART_ID,
       r.UPDATE_STAFF_ID,
       r.UPDATE_TIME,
       r.REMARK
  FROM TF_B_CTRM_REFUND r
   WHERE (r.TID = :TID or :TID is null)
   AND r.REFUND_TIME between to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') and
       to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')
   and exists(select 1 from TF_B_CTRM_REFUND_SUB s,TF_B_CTRM_ORDER o
      where r.refund_id = s.REFUND_ID
      and s.oid = o.oid
      and (o.phone = :SERIAL_NUMBER OR :SERIAL_NUMBER is null))