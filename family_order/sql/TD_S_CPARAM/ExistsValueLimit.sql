--IS_CACHE=Y
SELECT COUNT(1) recordcount FROM td_s_commpara
WHERE subsys_code='CSM'
  AND param_attr=2001
  AND param_code=:PARAM_CODE
  AND PARA_CODE1=:PARA_CODE1
  AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
  AND SYSDATE BETWEEN start_date AND end_date