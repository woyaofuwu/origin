--IS_CACHE=N
SELECT NVL(DECODE(COUNT(1),0,0,1),0) 是否蒲公英互斥
  FROM TF_F_USER_DANDELION_RECV A, TD_S_COMMPARA B
 WHERE a.BIZ_TYPE_CODE = B.PARAM_CODE
   AND A.SERIAL_NUMBER_B = :SERIAL_NUMBER
   AND A.STATE_CODE = '1'
   AND B.SUBSYS_CODE = 'CSM'
   AND B.PARAM_ATTR = 1611
   AND B.PARA_CODE1 = :OPER_CODE
   AND SYSDATE BETWEEN B.START_DATE AND B.END_DATE
