--IS_CACHE=Y
SELECT NVL(DECODE(COUNT(1),0,0,1),0) 是否存在
 FROM TD_S_COMMPARA C
WHERE C.SUBSYS_CODE = 'CSM'
  AND C.PARAM_ATTR =:COMMPARA_ATTR
  AND C.PARAM_CODE = :COMMPARA_CODE
  AND C.PARA_CODE1 = :OPER_CODE
  AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE