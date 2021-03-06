SELECT TRADE_ID,
       ACCEPT_MONTH,
       USER_ID_A,
       SERIAL_NUMBER_A,
       USER_ID_B,
       SERIAL_NUMBER_B,
       RELATION_TYPE_CODE,
       ROLE_TYPE_CODE,
       ROLE_CODE_A,
       ROLE_CODE_B,
       ORDERNO,
       SHORT_CODE,
       INST_ID,
       TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,
       TO_CHAR(END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE,
       MODIFY_TAG,
       TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK
  FROM TF_F_TWO_CITY_WIDENET
 WHERE 1 = 1
   AND SERIAL_NUMBER_A = :SERIAL_NUMBER