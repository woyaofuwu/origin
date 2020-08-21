SELECT to_char(user_id) user_id,user_mode,recv_area_type,to_char(recv_user_id) recv_user_id,start_acyc_id,end_acyc_id,act_mode,service_id,to_char(fee_value1) fee_value1,to_char(fee_value2) fee_value2,to_char(fee_value3) fee_value3,to_char(fee_value4) fee_value4,to_char(fee_value5) fee_value5,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id 
  FROM tf_f_fixedfee_specinfo
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND service_id=:SERVICE_ID
   AND   to_char(sysdate,'YYYYMM')
   BETWEEN start_acyc_id AND end_acyc_id