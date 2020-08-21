UPDATE tf_f_user_callcust 
SET state=:NEW_STATE,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE  
WHERE callcust_type_code=:CALLCUST_TYPE_CODE 
AND serial_number=:SERIAL_NUMBER 
AND state=:OLD_STATE