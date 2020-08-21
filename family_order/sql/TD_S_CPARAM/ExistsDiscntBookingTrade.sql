SELECT count(1) recordcount
  FROM tf_b_trade
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND SUBSTR(process_tag_set,19,1) = '1'