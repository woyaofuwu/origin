--IS_CACHE=Y
SELECT A.PARAM_CODE,
       A.PARAM_NAME,
       A.PARA_CODE1,
       A.PARA_CODE2,
       A.PARA_CODE3,
       A.PARA_CODE4,
       A.PARA_CODE5
  FROM TD_S_COMMPARA A
 WHERE A.PARAM_ATTR = '4502'
   AND A.PARAM_CODE = :ITEM_ID
   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE