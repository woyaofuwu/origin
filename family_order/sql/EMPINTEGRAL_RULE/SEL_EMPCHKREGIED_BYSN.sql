--IS_CACHE=N
SELECT CUMU_ID 登记店员编码, TO_CHAR(SEND_TIME,'YYYY-MM-DD HH24:MI:SS') 登记时间,TO_CHAR(SEND_TIME,'YYYYMM') 已登记月份,PRE_NUM2 业务类型,PRE_CHAR2 台账标识
FROM CHNL_CU_REGI_PARALLEL A
 WHERE A.REGI_NUM =  (SELECT MAX(REGI_NUM)
FROM CHNL_CU_REGI_PARALLEL R
WHERE R.MOBILE_NUM=:SERIAL_NUMBER
 AND R.OPER_CODE=:OPER_CODE
)