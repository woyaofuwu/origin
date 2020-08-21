select 'ZZZZ' paracode,'所有地市' paraname from dual
UNION
SELECT AREA_CODE paracode,AREA_NAME paraname FROM td_m_area
 WHERE  (:TRADE_EPARCHY_CODE is null or :TRADE_EPARCHY_CODE is not null)
   AND AREA_LEVEL=20