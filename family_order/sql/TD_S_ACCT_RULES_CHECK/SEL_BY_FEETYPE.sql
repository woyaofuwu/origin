--IS_CACHE=Y
SELECT SYS_CODE,
       OPER_TYPE_CODE,
       FEE_TYPE_CODE,
       COLUMN_NAME,
       COLUMN_VALUE,
       PARA_TYPE,
       BEGIN_DATE,
       END_DATE
  FROM TD_S_ACCT_RULES_CHECK
 WHERE SYS_CODE = :SYS_CODE
   AND OPER_TYPE_CODE = :OPER_TYPE_CODE
   AND FEE_TYPE_CODE = :FEE_TYPE_CODE
   AND PARA_TYPE = '0'
   AND SYSDATE BETWEEN BEGIN_DATE AND END_DATE