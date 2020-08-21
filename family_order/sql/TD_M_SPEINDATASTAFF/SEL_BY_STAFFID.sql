--IS_CACHE=Y
SELECT staff_id,depart_id,depart_code,user_depart_code,depart_name,in_depart_id,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark,update_staff_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_m_speindatastaff
 WHERE staff_id=:STAFF_ID