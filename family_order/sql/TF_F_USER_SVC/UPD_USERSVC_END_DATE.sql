UPDATE tf_f_user_svc a 
SET a.end_date =to_date(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),update_time=sysdate
WHERE USER_ID=TO_NUMBER(:USER_ID)
AND PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000)
AND a.service_id =:SERVICE_ID
AND a.start_date=to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS')