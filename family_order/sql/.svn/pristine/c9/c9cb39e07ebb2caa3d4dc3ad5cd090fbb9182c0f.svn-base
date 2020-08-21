UPDATE tf_f_user_svcstate
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=SYSDATE, update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND service_id+0 = :SERVICE_ID
   AND state_code = :STATE_CODE
   AND end_date+0 > TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')