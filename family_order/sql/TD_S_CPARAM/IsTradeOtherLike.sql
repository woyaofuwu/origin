SELECT COUNT(1) recordcount
  FROM tf_b_trade_other
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND (rsrv_str1=:RSRV_STR1 OR :RSRV_STR1 IS NULL OR rsrv_str1 like :RSRV_STR1)
   AND (rsrv_str2=:RSRV_STR2 OR :RSRV_STR2 IS NULL)
   AND (rsrv_str3=:RSRV_STR3 OR :RSRV_STR3 IS NULL)
   AND (rsrv_str4=:RSRV_STR4 OR :RSRV_STR4 IS NULL)
   AND (rsrv_str5=:RSRV_STR5 OR :RSRV_STR5 IS NULL)
   AND (modify_tag=:MODIFY_TAG OR :MODIFY_TAG='*')