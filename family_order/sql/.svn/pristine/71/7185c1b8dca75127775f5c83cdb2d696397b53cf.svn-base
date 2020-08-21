--IS_CACHE=N
 SELECT D.DEPART_KIND_CODE 部门类型, TO_CHAR(T.ACCEPT_DATE, 'YYYYMM') 受理月份, T.TRADE_DEPART_ID 受理部门,T.TRADE_ID 台账标识
          FROM TD_M_DEPART D, ucr_crm1.TF_BH_TRADE T
         WHERE T.TRADE_ID IN
               (SELECT TRADE_ID
                  FROM (SELECT A.TRADE_ID
                          FROM ucr_crm1.TF_BH_TRADE A, TD_M_DEPART B
                         WHERE A.USER_ID = TO_NUMBER(:USER_ID)
                           AND TRUNC(A.ACCEPT_DATE) BETWEEN TRUNC(SYSDATE - 3) AND TRUNC(SYSDATE)
                           and a.accept_month = to_number(to_char(sysdate,'mm'))
                           AND A.CANCEL_TAG = '0'
                           AND A.TRADE_TYPE_CODE IN (330, 350, 329, 339, 349, 388, 389)
                           AND A.TRADE_ID NOT IN
                               (SELECT R.PRE_CHAR2
                                  FROM CHNL_CU_REGI_PARALLEL R
                                 WHERE R.OPER_CODE = '119'
                                   AND R.MOBILE_NUM =:SERIAL_NUMBER)
                           AND A.TRADE_DEPART_ID = B.DEPART_ID
                           AND (B.DEPART_KIND_CODE NOT IN ('100', '500','201') OR
                               A.TRADE_STAFF_ID IN ('ITFCHG00', 'ITFWGPT1','IBOSS000', 'ITFWEB00'))
                           AND NOT EXISTS
                         (SELECT 1
                                  FROM ucr_crm1.TF_B_TRADE_SCORE C
                                 WHERE A.TRADE_ID = C.TRADE_ID
                                   AND EXISTS
                                 (SELECT 1
                                          FROM TD_S_COMMPARA D
                                         WHERE D.SUBSYS_CODE = 'CSM'
                                           AND D.PARAM_ATTR = '97'
                                           AND D.END_DATE > SYSDATE
                                           AND D.PARAM_CODE = C.RULE_ID))
                         ORDER BY A.ACCEPT_DATE DESC)
                 WHERE ROWNUM < 2)
           AND T.TRADE_DEPART_ID = D.DEPART_ID