SELECT /*+ INDEX (tf_f_user_otherserv,idx_tf_f_user_otherserv_mode)*/ partition_id,to_char(user_id) user_id,service_mode,serial_number,process_info,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,(select b.data_name from TD_S_STATIC b where b.type_id = 'REPAIR_POST_INFO_REASON' and b.data_id = to_char(a.rsrv_num1) ) rsrv_str9, rsrv_str10,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,process_tag,staff_id,depart_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark 
  FROM tf_f_user_otherserv PARTITION(PAR_TF_F_USER_OTHERSERV_3) a
 WHERE service_mode=:SERVICE_MODE
   AND process_tag=:PROCESS_TAG
   AND end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND (start_date>=TO_DATE(:RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') OR :RSRV_DATE2 IS NULL)
   AND (start_date<=TO_DATE(:RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') OR :RSRV_DATE3 IS NULL)