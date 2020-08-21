--IS_CACHE=Y
SELECT area_code,area_name,area_frame 
  FROM td_m_area
 WHERE area_code=:AREA_CODE
   AND validflag='0'