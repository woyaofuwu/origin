--IS_CACHE=Y
SELECT A.PARA_CODE1||A.PARA_CODE2||A.PARA_CODE3||A.PARA_CODE4||A.PARA_CODE5||A.PARA_CODE6||A.PARA_CODE7||A.PARA_CODE8||A.PARA_CODE9||A.PARA_CODE10 AS SMS_CONTENT
 FROM TD_S_COMMPARA A
WHERE A.SUBSYS_CODE= :SUBSYS_CODE
AND A.PARAM_ATTR= :PARAM_ATTR
AND A.PARAM_CODE= :PARAM_CODE
AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE