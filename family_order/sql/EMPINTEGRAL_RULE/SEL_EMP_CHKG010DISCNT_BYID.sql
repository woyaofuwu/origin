--IS_CACHE=N
SELECT D.DEPART_KIND_CODE 部门类型, TO_CHAR(A.ACCEPT_DATE, 'YYYYMM') 受理月份,A.TRADE_DEPART_ID 受理部门,A.TRADE_STAFF_ID 受理工号,A.TRADE_ID 台账标识
FROM UCR_CRM1.TF_BH_TRADE       A,
      UCR_CRM1.TF_B_TRADE_DISCNT B,
      TF_F_USER_DISCNT  C,
      TD_M_DEPART       D
WHERE A.TRADE_ID = B.TRADE_ID
  AND A.TRADE_TYPE_CODE IN (10, 110)
  AND A.CANCEL_TAG = '0'
  AND B.DISCNT_CODE IN (4900, 4902)
  AND B.MODIFY_TAG = '0'
  AND B.USER_ID = C.USER_ID
  AND B.INST_ID = C.INST_ID
  AND A.USER_ID = TO_NUMBER(:USER_ID)
  AND B.END_DATE > SYSDATE
  AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE
  AND TRUNC(A.ACCEPT_DATE) BETWEEN TRUNC(SYSDATE - 3) AND TRUNC(SYSDATE)
  AND A.TRADE_DEPART_ID = D.DEPART_ID
  AND ROWNUM < 2