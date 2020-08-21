--IS_CACHE=Y
SELECT para_code1
  FROM td_s_commpara
 WHERE subsys_code=:SUBSYS_CODE
   AND param_attr=:PARAM_ATTR
   AND param_code=:PARAM_CODE
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')