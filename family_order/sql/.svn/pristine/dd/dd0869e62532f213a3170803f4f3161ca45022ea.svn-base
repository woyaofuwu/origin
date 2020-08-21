UPDATE tf_f_user_otherserv
   SET process_tag= :PROCESS_TAG2,
       rsrv_date1 = to_date(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss'),
       remark = :REMARK
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND rsrv_num1 = :RSRV_NUM1
   AND service_mode=:SERVICE_MODE
   AND process_tag=:PROCESS_TAG
   AND end_date > sysdate