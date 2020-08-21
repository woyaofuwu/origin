--IS CACHE=Y
SELECT F.DEPART_KIND_CODE 部门类型, TO_CHAR(A.ACCEPT_DATE, 'YYYYMM') 受理月份,A.TRADE_ID 台账标识
         FROM ucr_crm1.TF_BH_TRADE        A,
              ucr_crm1.TF_B_TRADE_PLATSVC B,
              TD_M_DEPART        F
        WHERE b.Service_Id IN ('98001901','80012675')
          AND ((TRUNC(A.ACCEPT_DATE) BETWEEN TRUNC(SYSDATE - 3) AND TRUNC(SYSDATE)) OR
                 (SYSDATE <= TO_DATE('2017-01-20 23:59:59', 'YYYY-MM-DD HH24:MI:SS') AND
                 A.ACCEPT_DATE BETWEEN TO_DATE('2016-12-30', 'YYYY-MM-DD') AND
                 TO_DATE('2017-01-20 23:59:59', 'YYYY-MM-DD HH24:MI:SS')))
          AND A.TRADE_TYPE_CODE IN (3700, 3788, '110', '252', '10', '240')
          AND A.CANCEL_TAG = '0'
          AND A.TRADE_ID = B.TRADE_ID
          AND A.TRADE_DEPART_ID = F.DEPART_ID
          and B.OPER_CODE = '06'
          AND A.USER_ID = TO_NUMBER(:USER_ID)
          and a.accept_month in
              (to_number(to_char(SYSDATE, 'mm')),
               to_number(to_char(add_months(SYSDATE, -1), 'mm')))
          AND ROWNUM < 2 