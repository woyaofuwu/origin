UPDATE tf_b_trade
   SET next_deal_tag=:NEXT_DEAL_TAG,process_tag_set=substr(process_tag_set,1,3)||'1'
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND cancel_tag = :CANCEL_TAG