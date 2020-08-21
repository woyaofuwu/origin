select PARTITION_ID,
       TO_CHAR(USER_ID_A) USER_ID_A,
       TO_CHAR(USER_ID_B) USER_ID_B,
       RELATION_TYPE_CODE,
       ROLE_CODE_A,
       ROLE_CODE_B,
       INST_ID,
       ORDERNO,
       SHORT_CODE,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       SERIAL_NUMBER_B,
       SERIAL_NUMBER_A
  from TF_F_RELATION_UU
 where USER_ID_B = TO_NUMBER(:USER_ID_B)
   and PARTITION_ID = mod(TO_NUMBER(:USER_ID_B), 10000)
   and RELATION_TYPE_CODE = :RELATION_TYPE_CODE
   and END_DATE>trunc(last_day(Sysdate)+1)