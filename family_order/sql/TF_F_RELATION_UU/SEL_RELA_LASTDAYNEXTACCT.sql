SELECT PARTITION_ID,
       TO_CHAR(USER_ID_A) USER_ID_A,
       SERIAL_NUMBER_A,
       TO_CHAR(USER_ID_B) USER_ID_B,
       SERIAL_NUMBER_B,
       RELATION_TYPE_CODE,
       ROLE_CODE_A,
       ROLE_CODE_B,
       ORDERNO,
       SHORT_CODE,
       TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,
       TO_CHAR(END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE
  FROM TF_F_RELATION_UU
 WHERE USER_ID_A = TO_NUMBER(:USER_ID_A)
   AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE
   AND ROLE_CODE_B = :ROLE_CODE_B
   AND END_DATE > to_date(:FIRST_DATE_NEXTACCT,'YYYY-MM-DD HH24:MI:SS')