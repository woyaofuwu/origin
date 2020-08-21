SELECT COUNT(1) recordcount
  FROM tf_b_trade a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   and accept_month = to_number(substr(:TRADE_ID,5,2))
   AND (rsrv_str1=:RSRV_STR1 OR :RSRV_STR1 IS NULL OR rsrv_str1 like :RSRV_STR1)
   AND (rsrv_str7=:RSRV_STR2 OR :RSRV_STR2 IS NULL OR rsrv_str7 like :RSRV_STR2)
   AND serial_number_b IS NULL