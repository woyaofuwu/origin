--IS_CACHE=N
SELECT NVL(DECODE(COUNT(1),0,0,1),0) 是否受理
FROM UCR_CRM1.TF_B_TRADE_RES A
WHERE A.TRADE_ID = :TRADE_ID
AND A.MODIFY_TAG = :MODIFY_TAG
AND EXISTS (SELECT 1 FROM TD_S_COMMPARA C
WHERE 1=1
AND C.SUBSYS_CODE = 'CSM'
AND C.PARAM_ATTR = 106
AND C.PARAM_CODE = 'CUMU'
AND C.PARA_CODE1 = A.RSRV_STR1)