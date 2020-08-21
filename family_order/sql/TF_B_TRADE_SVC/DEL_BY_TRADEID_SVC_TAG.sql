DELETE FROM tf_b_trade_svc
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND service_id=:SERVICE_ID
   AND modify_tag=:MODIFY_TAG