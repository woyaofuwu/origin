--IS_CACHE=Y
SELECT province_code,area_code,area_name,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,biz_status,remark,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 
  FROM td_m_sparea
 WHERE biz_status = :BIZ_STATUS
   AND area_code = :AREA_CODE
   AND SYSDATE > start_date