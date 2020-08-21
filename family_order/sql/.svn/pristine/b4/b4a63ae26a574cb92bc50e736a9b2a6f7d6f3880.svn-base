SELECT partition_id,to_char(user_id) user_id,service_id,main_tag,state_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
    FROM TF_F_USER_SVCSTATE
   WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
     AND USER_ID = TO_NUMBER(:USER_ID)
     AND SERVICE_ID = 0
     AND STATE_CODE IN ('5', '7', 'A', 'B', 'C', 'D')
     AND END_DATE >= SYSDATE