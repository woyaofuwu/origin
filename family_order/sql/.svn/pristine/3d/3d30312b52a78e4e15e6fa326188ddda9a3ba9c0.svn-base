SELECT '0' trade_id,0 accept_month,to_char(user_id) user_id,service_id,state_code,'A' modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_user_svcstate a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date > SYSDATE
   AND start_date < end_date
   AND NOT EXISTS (SELECT 1 FROM tf_b_trade_svcstate
                    WHERE trade_id=TO_NUMBER(:TRADE_ID)
                      AND accept_month = :ACCEPT_MONTH
                      AND service_id = a.service_id
                      AND user_id = TO_NUMBER(:USER_ID)
                      AND state_code = a.state_code
                      --AND start_date = a.start_date
                      AND modify_tag = '1')
 UNION
 SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,service_id,state_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_b_trade_svcstate
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = :ACCEPT_MONTH
   AND user_id = TO_NUMBER(:USER_ID)
   AND modify_tag = '0'