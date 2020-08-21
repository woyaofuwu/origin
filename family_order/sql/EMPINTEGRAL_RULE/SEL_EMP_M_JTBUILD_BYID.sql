--IS_CACHE=N
SELECT COUNT(1) 用户关系数
  FROM TF_F_RELATION_UU T
 WHERE T.RELATION_TYPE_CODE = '45'
   AND T.USER_ID_B = TO_NUMBER(:USER_ID)
   AND T.ROLE_CODE_B = '1'
   AND T.START_DATE <=
       ADD_MONTHS(TO_DATE(TO_CHAR(SYSDATE, 'YYYYMM'), 'YYYYMM'), -2) +
       1 / 86400
   AND T.END_DATE >=
       TRUNC(LAST_DAY(ADD_MONTHS(SYSDATE, -1)) + 1) - 1 / 86400