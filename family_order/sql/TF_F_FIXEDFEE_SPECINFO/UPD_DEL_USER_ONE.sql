UPDATE tf_f_fixedfee_specinfo
   SET end_acyc_id=:END_ACYC_ID,update_time=sysdate,update_depart_id=:UPDATE_DEPART_ID,update_staff_id=:UPDATE_STAFF_ID  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND user_mode=:USER_MODE
   AND end_acyc_id>(select acyc_id from TD_A_ACYCPARA where sysdate BETWEEN acyc_start_time AND acyc_end_time)
   AND act_mode=:ACT_MODE
   AND service_id=:SERVICE_ID