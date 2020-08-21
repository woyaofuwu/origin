UPDATE tf_f_fixedfee_specinfo
   SET modify_tag='2', end_acyc_id=:END_ACYC_ID,update_time=TO_DATE(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'),update_depart_id=:UPDATE_DEPART_ID,update_staff_id=:UPDATE_STAFF_ID  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND user_mode='1'
   AND act_mode='0'
   AND service_id=:SERVICE_ID
   AND to_char(sysdate,'YYYYMM')
   BETWEEN start_acyc_id AND end_acyc_id