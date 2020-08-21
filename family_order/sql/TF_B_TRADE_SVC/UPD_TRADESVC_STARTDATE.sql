UPDATE tf_b_trade_svc
   SET start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD')
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND user_id = TO_NUMBER(:USER_ID)
   AND service_id = :SERVICE_ID