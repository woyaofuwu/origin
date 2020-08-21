SELECT service_id,state_code,rsrv_str1
  FROM td_s_svcstate_trade_limit a

 WHERE EXISTS(SELECT 1 FROM tf_f_user_svcstate
               WHERE user_id=:USER_ID
                 AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                 AND service_id+0 = a.service_id
                 AND state_code=a.state_code
                 AND sysdate BETWEEN start_date+0 AND end_date+0)
                 AND trade_type_code=:TRADE_TYPE_CODE
                 AND limit_tag='1' AND sysdate BETWEEN start_date AND end_date
                 AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')