SELECT COUNT(*) recordcount
  FROM td_s_svcstate_trade_limit a
 WHERE trade_type_code=:TRADE_TYPE_CODE
    AND limit_tag='0' AND sysdate BETWEEN start_date AND end_date
    AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')
    AND (EXISTS(SELECT 1 FROM tf_f_user_svcstate
               WHERE user_id=:USER_ID
                 AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                 AND service_id+0 = a.service_id
                 AND state_code=a.state_code
                 AND sysdate BETWEEN start_date+0 AND end_date+0)
         OR EXISTS(SELECT /*+ ordered use_nl(f,e)*/1 FROM tf_b_trade f,tf_b_trade_svcstate e
                    WHERE e.trade_id = f.trade_id
                      AND e.accept_month = f.accept_month
                      AND e.user_id = f.user_id
                      AND f.user_id=:USER_ID
                      AND f.cancel_tag='0'
                      AND e.modify_tag='0'
                      AND e.service_id+0 = a.service_id
                      AND e.state_code=a.state_code))