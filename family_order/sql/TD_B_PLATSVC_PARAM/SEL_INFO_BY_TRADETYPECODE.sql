--IS_CACHE=Y
SELECT PARAM_CODE,
       PARA_CODE1,
       PARA_CODE2,
       PARA_CODE3,
       PARA_CODE4,
       PARA_CODE5,
       PARA_CODE6,
       PARA_CODE7,
       PARA_CODE8
  FROM TD_S_COMMPARA
 WHERE SUBSYS_CODE = 'CSM'
   AND PARAM_ATTR = '888'
   AND EPARCHY_CODE = 'ZZZZ'
   AND SYSDATE BETWEEN START_DATE AND END_DATE
   AND PARA_CODE1 = :PARA_CODE1