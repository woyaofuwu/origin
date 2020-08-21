--IS_CACHE=Y
SELECT code_area_code,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,0 x_tag 
  FROM td_m_codearea
 WHERE ((:CODE_AREA_CODE IS NOT NULL AND code_area_code=:CODE_AREA_CODE) OR :CODE_AREA_CODE IS NULL)