--IS_CACHE=Y
SELECT A.PARAM_NAME, A.PARA_CODE2
  FROM TD_S_COMMPARA A
 WHERE A.SUBSYS_CODE = 'CSM'
   AND A.PARAM_ATTR = '3699'
   AND A.PARAM_CODE = :BILL_TYPE
   AND A.PARA_CODE1 = 'DSMP'