SELECT COUNT(1) NUMB
  FROM TF_F_USER_PLATSVC T
 WHERE T.SERVICE_ID = :SERVICE_ID
   AND T.OPER_CODE = '19'
   AND T.BIZ_STATE_CODE = 'A'
   AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE
   AND T.USER_ID=:USER_ID