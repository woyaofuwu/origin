--IS_CACHE=Y
SELECT area_code paracode,area_name paraname
FROM td_m_area
WHERE parent_area_code=:TRADE_EPARCHY_CODE
AND area_level=30