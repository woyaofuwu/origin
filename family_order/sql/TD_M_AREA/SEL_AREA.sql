--IS_CACHE=Y
SELECT area_code,area_name
FROM td_m_area
 WHERE parent_area_code = :PARENT_AREA_CODE AND area_level=30
  ORDER BY area_code