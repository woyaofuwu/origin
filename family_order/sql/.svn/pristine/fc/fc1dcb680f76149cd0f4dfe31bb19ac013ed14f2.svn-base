UPDATE tf_f_user_otherserv
   SET process_tag='1',start_date=sysdate  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND service_mode=:SERVICE_MODE
   AND process_tag=:PROCESS_TAG
   AND rsrv_str2=:RSRV_STR2