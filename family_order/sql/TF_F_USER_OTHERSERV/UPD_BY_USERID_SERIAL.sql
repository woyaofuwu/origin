UPDATE tf_f_user_otherserv
   SET process_tag=:PROCESS_TAG,end_date=TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),
       staff_id=:STAFF_ID,depart_id=:DEPART_ID
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND TRIM(serial_number)=:SERIAL_NUMBER
   AND TRIM(service_mode)=:SERVICE_MODE
   AND TRIM(process_tag)='0'
   AND SYSDATE<end_date