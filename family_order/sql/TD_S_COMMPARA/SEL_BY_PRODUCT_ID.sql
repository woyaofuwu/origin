--IS_CACHE=Y
SELECT * FROM TD_S_COMMPARA WHERE PARAM_ATTR=:PARAM_ATTR AND PARAM_CODE=:PARAM_CODE
AND SYSDATE BETWEEN START_DATE AND END_DATE