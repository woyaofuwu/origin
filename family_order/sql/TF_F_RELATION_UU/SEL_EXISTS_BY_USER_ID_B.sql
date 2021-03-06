SELECT TO_CHAR(USER_ID_A) USER_ID_A,
       TO_CHAR(USER_ID_B) USER_ID_B,
       RELATION_TYPE_CODE,
       ROLE_CODE_A,
       ROLE_CODE_B,
       ORDERNO,
       SHORT_CODE,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       SERIAL_NUMBER_B,
       SERIAL_NUMBER_A
  FROM TF_F_RELATION_UU
 WHERE USER_ID_B = TO_NUMBER(:USER_ID_B)
   AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE
   AND SYSDATE < END_DATE + 0
   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000)
UNION ALL
SELECT TO_CHAR(A.USER_ID_A) USER_ID_A,
       TO_CHAR(A.USER_ID_B) USER_ID_B,
       A.RELATION_TYPE_CODE,
       A.ROLE_CODE_A,
       A.ROLE_CODE_B,
       A.ORDERNO,
       A.SHORT_CODE,
       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       A.SERIAL_NUMBER_B,
       A.SERIAL_NUMBER_A
  FROM TF_B_TRADE_RELATION A, TF_B_TRADE B
 WHERE A.USER_ID_B = TO_NUMBER(:USER_ID_B)
   AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE
   AND SYSDATE < A.END_DATE + 0
   AND A.TRADE_ID = B.TRADE_ID