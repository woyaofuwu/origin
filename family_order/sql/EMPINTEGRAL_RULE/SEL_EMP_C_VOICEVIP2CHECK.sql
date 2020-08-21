--IS CACHE=Y
SELECT D.DEPART_KIND_CODE 部门类型, TO_CHAR(A.ACCEPT_DATE, 'YYYYMM') 受理月份,A.TRADE_ID 台账标识
         FROM ucr_crm1.TF_BH_TRADE        A,
              ucr_crm1.TF_B_TRADE_PLATSVC B,
              TF_F_USER_PLATSVC  C,
              TD_M_DEPART        D
        WHERE A.TRADE_ID = B.TRADE_ID
          AND C.USER_ID = TO_NUMBER(:USER_ID)
          AND A.TRADE_TYPE_CODE IN (3700, 3788, '110', '252', '10')
          AND B.OPER_CODE IN ('01', '06')
          AND B.USER_ID = C.USER_ID
          AND A.CANCEL_TAG = '0'
          and b.service_id = '55627768'
          AND B.SERVICE_ID = C.SERVICE_ID
          AND B.START_DATE = C.START_DATE
          AND (TRUNC(A.ACCEPT_DATE) BETWEEN TRUNC(SYSDATE - 3) AND TRUNC(SYSDATE)
              OR (to_date('2017-05-08','yyyy-mm-dd')>SYSDATE AND A.ACCEPT_DATE>TO_date('2017-04-01','yyyy-mm-dd'))
          )
          AND A.TRADE_DEPART_ID = D.DEPART_ID
          AND b.end_date > SYSDATE
		  AND EXISTS (SELECT 1 FROM TD_CHL_COLLECT L WHERE L.COLLECT_ID=:COLLECT_ID AND L.COLLECT_DEF=to_char(B.SERVICE_ID))
          AND ROWNUM < 2