SELECT distinct CITY_CODE paracode,CITY_NAME paraname FROM citycode_chg
 WHERE (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)
 and update_time>sysdate
 ORDER BY paracode