SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,service_id,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_b_trade_svc 
 WHERE trade_id=TO_NUMBER(:NOW_TRADE_ID)
   AND accept_month=:ACCEPT_MONTH
   AND service_id IN (SELECT service_id FROM tf_b_trade_svc 
                WHERE trade_id = TO_NUMBER(:OLD_TRADE_ID)
                  AND accept_month=:ACCEPT_MONTH)