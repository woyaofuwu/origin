--IS_CACHE=Y
SELECT 'HlrServiceName' KEY, PARAM_CODE||'|'||PARA_CODE1 VALUE1, '' VALUE2, PARA_CODE2 VRESULT
FROM td_s_commpara
WHERE param_attr=600
AND eparchy_code='ZZZZ' AND SYSDATE BETWEEN start_date AND end_date
and subsys_code='CSM'
AND 'HlrServiceName'=:KEY