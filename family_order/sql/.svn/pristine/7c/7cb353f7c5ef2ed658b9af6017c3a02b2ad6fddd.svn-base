SELECT COUNT(*) recordcount
  FROM tf_b_trade_svc a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND service_id= TO_NUMBER(:SERVICE_ID)
   AND modify_tag='1'
   AND exists(select 1 from tf_f_user_svcstate b
               where a.user_id = b.user_id
                 and b.service_id = a.service_id
                 and b.state_code = :STATE_CODE
                 and sysdate between b.start_date and b.end_date)