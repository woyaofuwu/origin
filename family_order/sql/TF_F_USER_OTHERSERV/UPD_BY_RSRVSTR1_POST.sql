UPDATE tf_f_user_otherserv
   SET rsrv_date2=SYSDATE,process_tag='1',remark=:REMARK
 WHERE  rsrv_str1=:RSRV_STR1
   AND process_tag=:PROCESS_TAG
   AND service_mode=:SERVICE_MODE