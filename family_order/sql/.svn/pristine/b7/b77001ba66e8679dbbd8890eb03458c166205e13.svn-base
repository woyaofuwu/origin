UPDATE tf_b_trade_svc
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD')-1/24/3600
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = :ACCEPT_MONTH
   AND user_id = TO_NUMBER(:USER_ID)
   AND service_id = :SERVICE_ID