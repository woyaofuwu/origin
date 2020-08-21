SELECT A.NUMBER_OPRTYPE,A.ORDER_ID,A.SERVICENO,A.STATE,A.CREATE_TIME,A.RSRV_STR3||RSRV_STR4
FROM TF_B_CTRM_GERLSUBORDER A
WHERE A.NUMBER_OPRTYPE in('20','06')
AND A.CREATE_TIME BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')