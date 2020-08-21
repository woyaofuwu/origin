SELECT USER_ID,
       BOSS_SEQ,
       SERIAL_NUMBER,
       SERIAL_NUMBER_F,
       IMSI_F,
       SEC_COUNTRY,
       NETWORK_NAME,
       TO_CHAR(START_DATE,'YYYY-MM-DD hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE,'YYYY-MM-DD hh24:mi:ss') END_DATE,
       OPR_STATE,
       OPR_DATE,
       OPR_DATE_2,
       REMARK,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_DATE1,
       RSRV_DATE2,
       RSRV_DATE3
  FROM TF_F_USER_OCMSTW
 WHERE USER_ID = :USER_ID
   AND OPR_STATE in ( '01','03')
   AND sysdate between start_date and end_date