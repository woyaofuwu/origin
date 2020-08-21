SELECT A.USER_ID,
       A.DISCNT_CODE,
       B.PARAM_CODE,
       B.PARA_CODE1,
       B.PARA_CODE2,
       B.PARA_CODE3,
       B.PARA_CODE4,
       B.PARA_CODE5
  FROM TF_B_TRADE_DISCNT A, TD_S_COMMPARA B
 WHERE A.DISCNT_CODE = TO_NUMBER(B.PARAM_CODE)
   AND B.SUBSYS_CODE = 'CSM'
   AND B.PARAM_ATTR = 1655
   AND TO_CHAR(A.START_DATE, 'yyyymmdd') =
       TO_CHAR(LAST_DAY(SYSDATE) + 1, 'yyyymmdd')
   AND A.END_DATE > SYSDATE
   AND SYSDATE BETWEEN B.START_DATE AND B.END_DATE
   AND A.TRADE_ID = :TRADE_ID
   AND A.ACCEPT_MONTH = :ACCEPT_MONTH