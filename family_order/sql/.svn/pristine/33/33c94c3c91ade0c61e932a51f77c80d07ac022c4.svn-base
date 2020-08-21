UPDATE tf_b_trade
   SET next_deal_tag = :NEXT_DEAL_TAG,exec_time=SYSDATE
 WHERE trade_id = to_number(:TRADE_ID)
   AND cancel_tag = :CANCEL_TAG