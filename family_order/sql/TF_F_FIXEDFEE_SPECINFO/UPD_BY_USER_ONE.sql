UPDATE tf_f_fixedfee_specinfo
   SET end_acyc_id=:END_ACYC_ID,fee_value1=TO_NUMBER(:FEE_VALUE1),fee_value2=TO_NUMBER(:FEE_VALUE2),fee_value3=TO_NUMBER(:FEE_VALUE3),fee_value4=TO_NUMBER(:FEE_VALUE4),fee_value5=TO_NUMBER(:FEE_VALUE5),update_time=sysdate,update_depart_id=:UPDATE_DEPART_ID,update_staff_id=:UPDATE_STAFF_ID  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND user_mode=:USER_MODE
   AND start_acyc_id=:START_ACYC_ID
   AND act_mode=:ACT_MODE
   AND service_id=:SERVICE_ID