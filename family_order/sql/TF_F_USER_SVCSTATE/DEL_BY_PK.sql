DELETE FROM tf_f_user_svcstate
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND service_id=:SERVICE_ID
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')