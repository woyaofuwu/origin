--IS_CACHE=N
SELECT E.DEPART_KIND_CODE 部门类型, TO_CHAR(A.ACCEPT_DATE, 'YYYYMM') 受理月份,A.TRADE_ID 台账标识
  FROM UCR_CRM1.TF_BH_TRADE       A,
       UCR_CRM1.TF_B_TRADE_DISCNT B,
       TF_F_USER_DISCNT  C,
       TD_M_DEPART       E
 WHERE A.USER_ID = TO_NUMBER(:USER_ID)
   AND B.USER_ID = TO_NUMBER(:USER_ID)
   AND A.TRADE_TYPE_CODE = '281'
   AND A.CANCEL_TAG = '0' --未返销
   AND A.TRADE_ID = B.TRADE_ID
   AND B.DISCNT_CODE = '3403'
   AND TRUNC(A.ACCEPT_DATE) BETWEEN TRUNC(SYSDATE - 3) AND TRUNC(SYSDATE) --20101008 QIAND 统一修改判断条件。
   AND B.END_DATE > SYSDATE
   AND B.MODIFY_TAG = '0'
   AND C.USER_ID = B.USER_ID
   AND B.INST_ID = C.INST_ID
   AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE
   AND A.TRADE_DEPART_ID = E.DEPART_ID
   AND ROWNUM < 2