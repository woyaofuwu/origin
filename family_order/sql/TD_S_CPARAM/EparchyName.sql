--IS_CACHE=Y
SELECT 'EparchyName'  KEY, area_code VALUE1,'-1' VALUE2,area_name VRESULT
  FROM td_m_area
 WHERE 'EparchyName' = :KEY