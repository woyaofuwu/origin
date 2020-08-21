UPDATE tf_f_user_callcust 
SET state='0',update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE  
WHERE callcust_type_code=:CALLCUST_TYPE_CODE 
AND (update_staff_id=:UPDATE_STAFF_ID
    OR update_time<SYSDATE-1)
AND state='2'