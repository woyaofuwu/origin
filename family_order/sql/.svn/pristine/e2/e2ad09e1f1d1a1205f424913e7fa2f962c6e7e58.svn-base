SELECT TO_CHAR(TRANS_ID) TRANS_ID,
       USER_ID,
       SERIAL_NUMBER,
       SERIAL_NUMBER_TEMP,
       EPARCHY_CODE,
       STATE,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       REMARK,
       '1' HIS,
       RSRV_STR6,
       RSRV_STR7
  FROM TF_F_SELFHELPCARD_FLOW
 WHERE TRANS_ID = :TRANS_ID 
UNION ALL
SELECT TO_CHAR(TRANS_ID) TRANS_ID,
       USER_ID,
       SERIAL_NUMBER,
       SERIAL_NUMBER_TEMP,
       EPARCHY_CODE,
       STATE,
       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       REMARK,
       '0' HIS,
       RSRV_STR6,
       RSRV_STR7
  FROM TF_FH_SELFHELPCARD_FLOW
 WHERE  TRANS_ID = :TRANS_ID