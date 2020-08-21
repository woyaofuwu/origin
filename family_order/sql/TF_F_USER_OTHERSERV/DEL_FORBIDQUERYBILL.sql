DELETE FROM tf_f_user_otherserv
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND service_mode = :SERVICE_MODE
   AND process_tag = :PROCESS_TAG
   AND serial_number = :SERIAL_NUMBER