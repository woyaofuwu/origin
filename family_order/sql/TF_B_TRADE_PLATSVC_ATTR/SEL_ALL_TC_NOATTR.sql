SELECT C.PARA_CODE2, C.PARA_CODE10
  FROM TF_B_TRADE_PLATSVC A, TD_S_COMMPARA C
 WHERE A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   AND A.TRADE_ID = TO_NUMBER(:TRADE_ID)
   AND A.USER_ID = :USER_ID
   AND A.SERVICE_ID = :SERVICE_ID
   AND C.SUBSYS_CODE = 'CSM'
   AND C.PARAM_ATTR = 3700
   AND C.PARAM_CODE = :SERVICE_ID
   AND C.PARA_CODE1 = 'noattr'