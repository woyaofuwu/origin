SELECT to_char(operate_id) operate_id,serial_number,to_char(user_id) user_id,biz_type_code,actioncode,homedomain,sys_code,user_state_code,send_status,to_char(send_date,'yyyy-mm-dd hh24:mi:ss') send_date,send_staff,deal_flag,deal_result 
  FROM tl_b_send_user_state
 WHERE (:OPERATE_ID is null or operate_id=TO_NUMBER(:OPERATE_ID))
   AND (:SERIAL_NUMBER is null or serial_number=:SERIAL_NUMBER)
   AND (:HOMEDOMAIN is null or homedomain=:HOMEDOMAIN)
   AND (:SEND_DATE is null or send_date=TO_DATE(:SEND_DATE, 'YYYY-MM-DD HH24:MI:SS'))
   AND (:SEND_STAFF is null or send_staff=:SEND_STAFF)