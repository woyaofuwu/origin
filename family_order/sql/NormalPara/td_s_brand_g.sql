--IS_CACHE=Y
SELECT BRAND_CODE paracode,BRAND paraname FROM td_s_brand
 WHERE sysdate BETWEEN start_date AND end_date
   AND (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)
   AND brand_code like 'G%'