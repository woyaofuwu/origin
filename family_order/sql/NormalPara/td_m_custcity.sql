--IS_CACHE=Y
SELECT CITY_CODE paracode,CITY_NAME paraname FROM td_m_custcity
 WHERE length(CUSTOM_CODE)>2
  AND (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)
 ORDER BY 1