UPDATE tf_b_trade_other
   SET RSRV_STR5=:RSRV_STR5
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND rsrv_value_code=:RSRV_VALUE_CODE