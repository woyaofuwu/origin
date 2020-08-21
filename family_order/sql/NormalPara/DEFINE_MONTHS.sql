--IS_CACHE=Y
SELECT value1 paracode,vresult paraname
  FROM td_s_cparam WHERE key='DefineMonths'
   AND (:TRADE_EPARCHY_CODE is null or :TRADE_EPARCHY_CODE is not null)
 ORDER BY 1