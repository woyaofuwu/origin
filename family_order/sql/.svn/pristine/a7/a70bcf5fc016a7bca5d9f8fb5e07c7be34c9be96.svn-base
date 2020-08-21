--IS_CACHE=Y
SELECT staff_id,external_sys_type,use_staff_id,staff_passwd,serial_number,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 
  FROM td_m_staff_id_relation
 WHERE staff_id=:STAFF_ID
   AND (:EXTERNAL_SYS_TYPE IS NULL OR external_sys_type=:EXTERNAL_SYS_TYPE)