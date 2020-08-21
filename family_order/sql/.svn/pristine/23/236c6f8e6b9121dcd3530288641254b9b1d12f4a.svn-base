SELECT callcust_type_code,serial_number,user_type_code,state,in_depart_id,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_user_callcust
 WHERE callcust_type_code=:CALLCUST_TYPE_CODE
   AND in_depart_id=:IN_DEPART_ID
   AND (:USER_TYPE_CODE IS NULL OR user_type_code=:USER_TYPE_CODE)
   AND state='0'