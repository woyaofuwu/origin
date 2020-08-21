update tf_f_user_svcallowance
set end_date=sysdate,update_date=sysdate
WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND (service_type = :SERVICE_TYPE OR :SERVICE_TYPE is NULL)
   AND sysdate between start_date and end_date