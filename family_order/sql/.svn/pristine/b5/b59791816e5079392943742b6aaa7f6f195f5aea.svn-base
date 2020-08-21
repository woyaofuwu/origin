UPDATE tf_bh_trade
   SET process_tag_set=:PROCESS_TAG_SET
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND cancel_tag = '0'