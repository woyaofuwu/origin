--IS_CACHE=Y
SELECT area_code,stock_level 
  FROM td_s_assignrule
 WHERE (eparchy_code=:EPARCHY_CODE or eparchy_code='ZZZZ')
   AND res_type_code=:RES_TYPE_CODE
   AND depart_id=:DEPART_ID