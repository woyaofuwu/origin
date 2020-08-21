SELECT USER_ID_A,
       SERIAL_NUMBER_A,
       TO_CHAR(USER_ID_B) USER_ID_B,
       SERIAL_NUMBER_B,
       ACTIVE_TYPE,
       INST_ID,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       GIVE_TAG,
       GIVE_DATE,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_DATE1,
       RSRV_DATE2,
       RSRV_DATE3,
       REMARK
  FROM TF_F_VPMNACTIVE_RELATION T
 WHERE T.USER_ID_A = TO_NUMBER(:USER_ID_A)
   AND T.ACTIVE_TYPE = :ACTIVE_TYPE
   AND T.GIVE_TAG = '0'
   AND T.START_DATE >= TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')
   AND T.START_DATE <= TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')
   AND T.END_DATE > SYSDATE