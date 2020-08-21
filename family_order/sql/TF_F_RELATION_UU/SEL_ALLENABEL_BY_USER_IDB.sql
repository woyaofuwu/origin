select TO_CHAR(USER_ID_A) USER_ID_A,
       TO_CHAR(USER_ID_B) USER_ID_B,
       RELATION_TYPE_CODE,
       ROLE_CODE_A,
       ROLE_CODE_B,
       ORDERNO,
       SHORT_CODE,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       SERIAL_NUMBER_A,
       SERIAL_NUMBER_B
  from TF_F_RELATION_UU U
 where USER_ID_B = :USER_ID_B
   and sysdate > sysdate
