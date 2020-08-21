UPDATE tf_b_trade
   SET next_deal_tag = '0',olcom_tag='0'
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND cancel_tag = :CANCEL_TAG