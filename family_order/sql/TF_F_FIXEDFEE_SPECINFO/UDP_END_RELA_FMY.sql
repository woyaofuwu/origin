UPDATE tf_f_fixedfee_specinfo
   SET end_acyc_id=:END_ACYC_ID,update_time=SYSDATE,update_depart_id=:UPDATE_DEPART_ID,update_staff_id=:UPDATE_STAFF_ID,modify_tag=:MODIFY_TAG,trade_id=TO_NUMBER(:TRADE_ID)  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND recv_user_id=TO_NUMBER(:RECV_USER_ID)
   AND user_mode=:USER_MODE
   AND recv_area_type=:RECV_AREA_TYPE
   AND act_mode=:ACT_MODE
   AND service_id=:SERVICE_ID
   AND (SELECT a.acyc_id FROM td_a_acycpara a WHERE SYSDATE BETWEEN a.acyc_start_time AND a.acyc_end_time)
   < end_acyc_id