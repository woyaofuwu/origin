SELECT COUNT(*) recordcount
  FROM tf_f_user_otherserv
 WHERE user_id=:USER_ID
   AND partition_id=mod(:USER_ID,10000)
   AND service_mode=:SERVICE_MODE
   AND process_tag='0'
   AND (start_date>to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') OR :START_DATE IS NULL)  --NULL 表示不判断start_date
   AND end_date>sysdate
   AND (instr(:RSRV_STR7,rsrv_str7) > 0 OR :RSRV_STR7='*')  --'*'表示不判断rsrv_str7