UPDATE tf_a_prechargebillinfo
   SET process_tag='1'  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND service_mode=:SERVICE_MODE
   AND process_tag=:PROCESS_TAG