SELECT trunc(sysdate,'dd') - trunc(open_date,'dd') RSRV_STR1
                       FROM TF_F_USER
                      WHERE user_id = TO_NUMBER(:USER_ID)
                        AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)