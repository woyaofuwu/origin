UPDATE tf_f_user_svcstate
   SET start_date=TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),
       end_date=TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),update_time=SYSDATE, update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND service_id = :SERVICE_ID
   AND state_code = :STATE_CODE
   AND sysdate between start_date and end_date