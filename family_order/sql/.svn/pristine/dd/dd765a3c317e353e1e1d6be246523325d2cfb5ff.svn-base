SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,service_id,state_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_b_trade_svcstate a
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag = '1'
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_svcstate
                    WHERE user_id = TO_NUMBER(:USER_ID)
                      AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                      AND SYSDATE BETWEEN start_date AND end_date
                      AND service_id = a.service_id
                      AND state_code = a.state_code)