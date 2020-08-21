UPDATE tf_bh_trade
   SET rsrv_str3 = :RSRV_STR3 
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND cancel_tag = '0'