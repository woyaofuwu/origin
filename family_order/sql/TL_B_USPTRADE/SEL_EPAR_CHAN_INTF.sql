SELECT A.EPARCHY_CODE,A.CHAN_ID,B.FUNC_DESC,TO_CHAR (MIN(START_TIME),'YYYY-MM-DD') START_TIME,TO_CHAR (MAX(END_TIME),'YYYY-MM-DD') END_TIME,
TO_CHAR (MIN(START_TIME),'HH24:MI:DD') S_TIME,TO_CHAR (MAX(END_TIME),'HH24:MI:DD') E_TIME,
COUNT(1) QUANTITY,ROUND(SUM(ROUND(TO_NUMBER(TO_DATE (TO_CHAR (END_TIME, 'YYYY-MM-DD HH24.MI.SS'), 'YYYY-MM-DD HH24.MI.SS')-
   TO_DATE (TO_CHAR (START_TIME, 'YYYY-MM-DD HH24.MI.SS'), 'YYYY-MM-DD HH24.MI.SS'))* 24 * 60 * 60))/COUNT(1)) AVG_TIME
FROM TL_B_USPTRADE A,TD_S_STAFF_BIZLIST B
WHERE A.BIZ_CODE=B.BIZ_CODE
  AND A.TRANS_CODE=B.TRANS_CODE
  AND A.RESULT_CODE='0'
  AND A.RSP_CODE='0'
  AND A.BIZ_CODE=:BIZ_CODE
  AND A.CHAN_ID=:CHAN_ID
  AND A.EPARCHY_CODE=:EPARCHY_CODE
  AND TO_CHAR(START_TIME,'HH24:MI:SS')>=:S_TIME
  AND TO_CHAR(END_TIME,'HH24:MI:SS')<=:E_TIME
  AND START_TIME>=TO_DATE(:START_TIME,'YYYY-MM-DD HH24.MI.SS')
  AND END_TIME<=TO_DATE(:END_TIME,'YYYY-MM-DD HH24.MI.SS')
GROUP BY A.EPARCHY_CODE,A.CHAN_ID,B.FUNC_DESC