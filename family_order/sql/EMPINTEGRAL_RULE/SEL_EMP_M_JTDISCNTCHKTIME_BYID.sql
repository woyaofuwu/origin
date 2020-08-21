--IS_CACHE=N
SELECT COUNT(1) 满一个月业务数
  FROM TF_F_RELATION_UU A, TF_F_RELATION_UU B
 WHERE A.USER_ID_A = B.USER_ID_A
   AND B.USER_ID_B = TO_NUMBER(:USER_ID)
   AND B.RELATION_TYPE_CODE = '45'
   AND B.ROLE_CODE_B = '1'
   AND B.START_DATE < TRUNC(LAST_DAY(ADD_MONTHS(SYSDATE, -2)) + 1)
   AND B.END_DATE >=
       TRUNC(LAST_DAY(ADD_MONTHS(SYSDATE, -1)) + 1) - 1 / 86400
   AND A.RELATION_TYPE_CODE = '45'
   AND A.ROLE_CODE_B = '2'
   AND A.START_DATE < TRUNC(LAST_DAY(ADD_MONTHS(SYSDATE, -2)) + 1)
   AND A.END_DATE >=
       TRUNC(LAST_DAY(ADD_MONTHS(SYSDATE, -1)) + 1) - 1 / 86400