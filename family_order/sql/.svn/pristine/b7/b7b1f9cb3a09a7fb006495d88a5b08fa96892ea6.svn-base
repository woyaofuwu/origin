DELETE FROM tf_b_trade_svcstate
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id = TO_NUMBER(:USER_ID)
   AND service_id = :SERVICE_ID
   AND state_code = :STATE_CODE