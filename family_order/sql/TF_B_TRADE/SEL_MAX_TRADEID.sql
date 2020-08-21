SELECT to_char(max(trade_id)) trade_id
  FROM tf_bh_trade
 WHERE trade_type_code=TO_NUMBER(:TRADE_TYPE_CODE)
   AND user_id=TO_NUMBER(:USER_ID)
   AND add_months(accept_date,to_number(:ACCEPT_DATE)) > SYSDATE
   AND cancel_tag=:CANCEL_TAG
GROUP BY trade_id