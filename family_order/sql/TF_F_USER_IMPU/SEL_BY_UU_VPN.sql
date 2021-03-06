SELECT U.PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, 
       U.SERIAL_NUMBER_A, TO_CHAR(USER_ID_B) USER_ID_B, 
       U.SERIAL_NUMBER_B, U.RELATION_TYPE_CODE, U.ROLE_CODE_A, 
       U.ROLE_CODE_B, U.ORDERNO, U.SHORT_CODE, 
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, 
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE
   FROM TF_F_RELATION_UU U
 WHERE 1 = 1
   AND U.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   AND U.USER_ID_B = TO_NUMBER(:USER_ID)
   AND U.RELATION_TYPE_CODE IN ('20', 'E1', 'E2', 'E3')
   AND SYSDATE BETWEEN U.START_DATE AND U.END_DATE
   AND U.END_DATE>LAST_DAY(TRUNC(SYSDATE))+1-1/24/3600
   AND ROWNUM < 2
   AND EXISTS (
                    SELECT 1 FROM TF_F_USER_IMPU I
                     WHERE U.USER_ID_B = I.USER_ID
                       AND I.PARTITION_ID = MOD(TO_NUMBER(U.USER_ID_B), 10000)
                       AND SYSDATE BETWEEN I.START_DATE AND I.END_DATE
                )