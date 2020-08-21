UPDATE tf_b_trade
   SET subscribe_state = :SUBSCRIBE_STATE,
       olcom_tag = :OLCOM_TAG,
       next_deal_tag = :NEXT_DEAL_TAG
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND cancel_tag = :CANCEL_TAG