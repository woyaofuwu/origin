SELECT to_char(t.accept_date,'yyyy-mm-dd') accept_date,t.trade_type_code,t.in_mode_code,t.trade_staff_id FROM tf_bh_trade t
WHERE 1=1
AND t.trade_id =
(SELECT to_char(MAX(trade_id)) trade_id
  FROM tf_bh_trade
 WHERE 1=1
   AND user_id=:USER_ID
   AND (trade_type_code=:TRADE_TYPE_CODE OR :TRADE_TYPE_CODE IS NULL)
   AND (cancel_tag=:CANCEL_TAG OR :CANCEL_TAG IS NULL)
   AND months_between(sysdate,accept_date)<7)