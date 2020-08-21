--IS_CACHE=Y
SELECT area_code,area_name,parent_area_code 	
  FROM td_m_area					
 WHERE					
 parent_area_code=:PARENT_AREA_CODE					
 and validflag='0'					
 and (start_date is null or sysdate>=start_date)