UPDATE tf_f_bankband
   SET update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),end_time=TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS'),reck_tag=:RECK_TAG  
 WHERE user_id=TO_NUMBER(:USER_ID)
    AND SYSDATE BETWEEN start_time AND end_time