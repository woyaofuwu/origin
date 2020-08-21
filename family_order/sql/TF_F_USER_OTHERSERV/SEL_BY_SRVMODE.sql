SELECT partition_id,to_char(user_id) user_id,service_mode,serial_number,process_info,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,process_tag,staff_id,depart_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark 
  FROM tf_f_user_otherserv a
 WHERE service_mode=:SERVICE_MODE
   AND process_tag=:PROCESS_TAG
  AND (:PROCESS_TAG<>'0' OR start_date BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') AND sysdate<end_date)
  AND (:PROCESS_TAG='0' OR end_date BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))
  AND (:PROCESS_TAG='0' OR NOT EXISTS(SELECT 1 FROM tf_f_user_otherserv b WHERE a.user_id=b.user_id AND b.service_mode =:SERVICE_MODE AND TRIM(process_tag)='0' AND sysdate<end_date))