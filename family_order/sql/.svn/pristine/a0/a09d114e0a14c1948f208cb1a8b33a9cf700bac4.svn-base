SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,service_id,state_code,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_b_trade_svcstate a
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND modify_tag = '0'
   AND EXISTS (SELECT 1 FROM tf_f_user_svcstate b,td_s_svcstate_limit c
                WHERE b.user_id = TO_NUMBER(:USER_ID)
                  AND b.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                  AND SYSDATE BETWEEN b.start_date AND b.end_date
                  AND b.service_id = a.service_id
                  AND b.state_code = c.state_code_a
                  AND c.service_id = b.service_id
                  AND c.state_code_b = a.state_code
                  AND c.limit_tag = '0'
                  AND (c.eparchy_code='ZZZZ' OR c.eparchy_code=:EPARCHY_CODE)
                  AND SYSDATE BETWEEN c.start_date AND c.end_date
                  AND NOT EXISTS (SELECT 1 FROM tf_b_trade_svcstate
                                   WHERE trade_id = TO_NUMBER(:TRADE_ID)
                                     AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                                     AND modify_tag = '1'
                                     AND service_id = b.service_id
                                     AND state_code = b.state_code))