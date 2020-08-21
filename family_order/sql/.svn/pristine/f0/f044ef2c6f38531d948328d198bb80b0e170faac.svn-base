UPDATE tf_f_user_svcstate
   SET state_code=:STATE_CODE,
       start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),
       end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND service_id = :SERVICE_ID
   AND end_date >= sysdate