SELECT to_char(MAX(trade_id)) trade_id 
  FROM tf_bh_trade
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND (trade_type_code=:TRADE_TYPE_CODE OR :TRADE_TYPE_CODE IS NULL)
   AND (cancel_tag=:CANCEL_TAG OR :CANCEL_TAG IS NULL)