UPDATE tf_bh_trade
   SET rsrv_str10 = :RSRV_STR10
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND cancel_tag = '0'