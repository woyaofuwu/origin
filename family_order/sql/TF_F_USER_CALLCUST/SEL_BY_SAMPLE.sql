SELECT callcust_type_code,serial_number,user_type_code,state,in_depart_id,update_staff_id,update_depart_id,TO_CHAR(update_time,'YYYY-MM-DD HH24:MI:SS') update_time,user_type_code 
FROM tf_f_user_callcust SAMPLE(10) 
WHERE callcust_type_code=:CALLCUST_TYPE_CODE 
AND in_depart_id=:IN_DEPART_ID 
AND state=:STATE 
AND (user_type_code=:USER_TYPE_CODE OR :USER_TYPE_CODE IS NULL)
AND ROWNUM=1
ORDER BY update_time