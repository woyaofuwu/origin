UPDATE tf_f_user_otherserv
   SET process_tag='1',end_date=sysdate,remark='返销'     
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=mod(TO_NUMBER(:USER_ID),10000) 
   AND TRIM(service_mode)=:SERVICE_MODE
   AND rsrv_str8=to_char(:TRADE_ID)