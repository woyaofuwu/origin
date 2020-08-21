UPDATE tf_f_user_otherserv
   SET process_tag='1',end_date=sysdate  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND service_mode='0A'
   AND process_tag='0'
   AND end_date>sysdate