SELECT RSRV_STR1,RSRV_STR2,RSRV_STR3 FROM 
(SELECT A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3
FROM TF_B_TRADE_OTHER A
WHERE 1 = 1
AND A.TRADE_ID = :TRADE_ID 
AND A.RSRV_VALUE_CODE = :PIC_ID_TAG
ORDER BY UPDATE_TIME DESC)
WHERE ROWNUM = 1