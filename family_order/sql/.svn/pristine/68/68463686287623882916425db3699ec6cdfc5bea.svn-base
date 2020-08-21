UPDATE tf_b_trade
   SET subscribe_state = :SUBSCRIBE_STATE,
       finish_date = DECODE(:SUBSCRIBE_STATE,'9',SYSDATE,NULL)
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND cancel_tag = :CANCEL_TAG