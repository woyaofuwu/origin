UPDATE tf_f_fixedfee_specinfo
   SET end_acyc_id=:NEW_END_ACYC_ID,update_time=SYSDATE,update_depart_id=:UPDATE_DEPART_ID,update_staff_id=:UPDATE_STAFF_ID  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND start_acyc_id=:START_ACYC_ID
   AND end_acyc_id=:END_ACYC_ID