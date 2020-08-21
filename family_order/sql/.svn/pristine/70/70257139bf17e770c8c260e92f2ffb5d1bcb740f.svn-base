UPDATE tf_f_user_otherserv
   SET process_info=:PROCESS_INFO,rsrv_num1=rsrv_num1+:RSRV_NUM1,rsrv_num2=decode(rsrv_num2+:RSRV_NUM2,-1000,0,rsrv_num2+:RSRV_NUM2),rsrv_str1=:RSRV_STR1,rsrv_date1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS'),staff_id=:STAFF_ID,depart_id=:DEPART_ID,remark=:REMARK  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND service_mode=:SERVICE_MODE
   AND TRIM(process_tag)='0'
   AND sysdate<end_date