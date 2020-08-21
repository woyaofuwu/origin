--IS_CACHE=Y
SELECT eparchy_code,moffice_id,switch_id,serialnumber_s,serialnumber_e,imsi_s,imsi_e,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7 
  FROM td_m_moffice
 where eparchy_code=:EPARCHY_CODE
   AND serialnumber_s<=:SERIALNUMBER_S
   AND serialnumber_e>=:SERIALNUMBER_E