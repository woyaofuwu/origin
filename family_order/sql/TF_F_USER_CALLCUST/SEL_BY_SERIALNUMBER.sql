SELECT callcust_type_code,serial_number,user_type_code,state,in_depart_id,update_staff_id,update_depart_id,TO_CHAR(update_time,'YYYY-MM-DD HH24:MI:SS') update_time,user_type_code 
FROM tf_f_user_callcust 
WHERE serial_number=:SERIAL_NUMBER