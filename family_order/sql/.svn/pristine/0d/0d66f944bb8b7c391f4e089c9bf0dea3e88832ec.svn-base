UPDATE tf_f_user_otherserv
   SET rsrv_date2=SYSDATE,process_tag='1',remark=:REMARK 
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND service_mode=:SERVICE_MODE
   AND serial_number=:SERIAL_NUMBER
   AND rsrv_str1=:RSRV_STR1
   AND process_tag=:PROCESS_TAG
   AND end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')