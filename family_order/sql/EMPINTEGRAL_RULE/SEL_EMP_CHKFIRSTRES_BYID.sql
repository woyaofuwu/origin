--IS CACHE=N
SELECT NVL(DECODE(COUNT(1),0,0,1),0) 是否4G用户
FROM ucr_crm1.TF_B_TRADE_RES A
WHERE A.TRADE_ID = :TRADE_ID
AND A.RSRV_TAG3 = '1'