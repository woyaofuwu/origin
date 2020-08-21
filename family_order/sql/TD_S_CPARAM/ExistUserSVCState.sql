SELECT count(1) recordcount FROM tf_f_user_svcstate
               WHERE user_id=:USER_ID
                 AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                 AND state_code=:STATE_CODE
                 AND service_id=:SERVICE_ID
                 AND sysdate BETWEEN start_date+0 AND end_date+0