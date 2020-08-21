SELECT COUNT(*) recordcount
  FROM tf_b_trade_svcstate
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND service_id=:SERVICE_ID
   AND state_code=:STATE_CODE
   AND modify_tag=:MODIFY_TAG