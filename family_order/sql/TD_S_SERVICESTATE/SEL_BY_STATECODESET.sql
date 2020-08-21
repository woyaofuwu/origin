SELECT B.SERVICE_ID,B.STATE_CODE,B.STATE_NAME FROM TD_S_SERVICESTATE b
WHERE b.service_id IN (SELECT a.service_id 
                      FROM tf_f_user_svc a 
                      WHERE a.main_tag='1' 
                      AND a.user_id=:USER_ID
                      AND a.partition_id=MOD(:USER_ID,10000)
                      AND SYSDATE BETWEEN a.start_date AND a.end_date)
AND b.state_code=:STATE_CODE