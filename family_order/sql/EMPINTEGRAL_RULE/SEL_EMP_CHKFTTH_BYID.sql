--IS_CACHE=N
  SELECT DEPART_KIND_CODE 部门类型, ACCEPT_DATE 受理月份, TRADE_DEPART_ID 受理部门, TRADE_ID 台账标识
   FROM (SELECT E.DEPART_KIND_CODE,
                 TO_CHAR(A.ACCEPT_DATE, 'YYYYMM') ACCEPT_DATE,
                 A.TRADE_DEPART_ID,
                 A.TRADE_ID
            FROM ucr_crm1.TF_BH_TRADE A, TD_M_DEPART E
           WHERE A.TRADE_DEPART_ID = E.DEPART_ID
             AND A.SERIAL_NUMBER=:SERIAL_NUMBER
             AND A.TRADE_TYPE_CODE IN (230,240)
             AND A.RSRV_STR1 IN ('67220428')
             AND A.CANCEL_TAG = '0'
             AND TRUNC(A.ACCEPT_DATE) BETWEEN TRUNC(SYSDATE - 3) AND TRUNC(SYSDATE)
          UNION
          SELECT D.DEPART_KIND_CODE,
                TO_CHAR(B.ACCEPT_DATE, 'YYYYMM') ACCEPT_DATE,
                B.TRADE_DEPART_ID,
                B.TRADE_ID
           FROM ucr_crm1.TF_B_TRADE_DISCNT A, ucr_crm1.TF_BH_TRADE B, TD_M_DEPART D
          WHERE B.USER_ID =
                (SELECT A.USER_ID
                   FROM TF_F_USER A
                  WHERE A.SERIAL_NUMBER =:KD_SERIAL_NUMBER
                    AND A.REMOVE_TAG = 0)
            AND B.TRADE_TYPE_CODE IN (613,600)
            AND B.CANCEL_TAG = '0'
            AND A.TRADE_ID = B.TRADE_ID
            AND TRUNC(B.ACCEPT_DATE) BETWEEN TRUNC(SYSDATE - 3) AND TRUNC(SYSDATE)
            AND A.MODIFY_TAG = '0'
            AND B.TRADE_DEPART_ID = D.DEPART_ID
            AND EXISTS (SELECT 1
                   FROM TD_S_COMMPARA C
                  WHERE C.SUBSYS_CODE = 'CSM'
                    AND C.PARAM_ATTR = 150
                    AND C.PARAM_CODE = 'CUMU'
                    AND C.PARA_CODE1 = A.DISCNT_CODE
                    AND C.END_DATE > SYSDATE)
            AND ROWNUM < 2
         UNION
         SELECT D.DEPART_KIND_CODE,
                TO_CHAR(B.ACCEPT_DATE, 'YYYYMM') ACCEPT_DATE,
                B.TRADE_DEPART_ID,
                B.TRADE_ID
           FROM ucr_crm1.TF_B_TRADE_DISCNT A, ucr_crm1.TF_B_TRADE B, TD_M_DEPART D
          WHERE B.USER_ID = TO_NUMBER(:USER_ID)
            AND B.TRADE_TYPE_CODE IN (613,600)
            AND B.CANCEL_TAG = '0'
            AND A.TRADE_ID = B.TRADE_ID
            AND TRUNC(B.ACCEPT_DATE) BETWEEN TRUNC(SYSDATE - 3) AND TRUNC(SYSDATE)
            AND A.MODIFY_TAG = '0'
            AND B.TRADE_DEPART_ID = D.DEPART_ID
            AND EXISTS (SELECT 1
                   FROM TD_S_COMMPARA C
                  WHERE C.SUBSYS_CODE = 'CSM'
                    AND C.PARAM_ATTR = 150
                    AND C.PARAM_CODE = 'CUMU'
                    AND C.PARA_CODE1 = A.DISCNT_CODE
                    AND C.END_DATE > SYSDATE)
            AND ROWNUM < 2) TRADE
  WHERE ROWNUM < 2