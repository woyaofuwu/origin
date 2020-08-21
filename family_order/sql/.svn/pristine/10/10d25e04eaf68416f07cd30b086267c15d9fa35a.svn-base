UPDATE tf_f_user_otherserv
   SET process_info=:PROCESS_INFO,rsrv_num2=rsrv_num2+:RSRV_NUM2,process_tag=:PROCESS_TAG,staff_id=:STAFF_ID,depart_id=:DEPART_ID,end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),remark=:REMARK  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND service_mode=:SERVICE_MODE
   AND rsrv_str1 = :RSRV_STR1
   AND TRIM(process_tag)='0'
   AND sysdate<end_date