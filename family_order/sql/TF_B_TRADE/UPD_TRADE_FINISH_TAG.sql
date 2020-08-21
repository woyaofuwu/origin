UPDATE tf_b_trade
   SET subscribe_state=:SUBSCRIBE_STATE,finish_date=SYSDATE
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND cancel_tag=:CANCEL_TAG