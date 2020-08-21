SELECT COUNT(1) recordcount
  FROM tf_b_trade
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND trade_type_code=110
   AND SUBSTR(process_tag_set,3,1)='1'