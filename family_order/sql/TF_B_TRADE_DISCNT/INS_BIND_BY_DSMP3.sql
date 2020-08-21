INSERT INTO TF_B_TRADE_DISCNT
  (TRADE_ID,
   ACCEPT_MONTH,
   ID,
   
   DISCNT_CODE,
   MODIFY_TAG,
   START_DATE,
   END_DATE)
  (SELECT TO_NUMBER(:TRADE_ID),
          TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)),
          A.USER_ID,
          '1',
          B.PARA_CODE5,
          '0',
          DECODE(B.PARA_CODE4,
                 '0',
                 SYSDATE,
                 '2',
                 TRUNC(ADD_MONTHS(SYSDATE, 1), 'mm'),
                 '3',
                 TRUNC(SYSDATE + 1, 'DD'),
                 '4',
                 ((SELECT SYSDATE
                     FROM DUAL
                    WHERE TO_CHAR((SYSDATE), 'dd') <
                          LPAD(B.PARA_CODE6, 2, '0')) UNION ALL
                  (SELECT TRUNC(ADD_MONTHS(SYSDATE, 1), 'mm')
                     FROM DUAL
                    WHERE TO_CHAR((SYSDATE), 'dd') >
                          LPAD(B.PARA_CODE6, 2, '0'))),
                 SYSDATE),
          TO_DATE('2050-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS')
     FROM TF_B_TRADE A, TD_S_COMMPARA B, TD_M_SP_BIZ C
    WHERE A.TRADE_ID = :TRADE_ID
      AND B.PARAM_ATTR = 908
      AND B.PARAM_CODE = :SP_CODE
      AND B.PARA_CODE1 = :BIZ_CODE
      AND B.PARA_CODE2 = '1'
      AND (B.EPARCHY_CODE = A.TRADE_EPARCHY_CODE OR B.EPARCHY_CODE = 'ZZZZ')
      AND C.SP_CODE = B.PARAM_CODE
      AND C.BIZ_CODE = B.PARA_CODE1
      AND EXISTS
    (SELECT 1 FROM TD_B_DISCNT WHERE DISCNT_CODE = B.PARA_CODE5)
      AND NOT EXISTS
    (SELECT 1
             FROM TF_B_TRADE_DISCNT
            WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
              AND ACCEPT_MONTH = TO_NUMBER(SUBSTRB(:TRADE_ID, 5, 2))
              AND DISCNT_CODE = B.PARA_CODE5
              AND MODIFY_TAG = '0')) UNION ALL
  (SELECT TO_NUMBER(:TRADE_ID),
          TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)),
          A.USER_ID,
          '1',
          B.PARA_CODE5,
          '0',
          DECODE(B.PARA_CODE4,
                 '0',
                 SYSDATE,
                 '2',
                 TRUNC(ADD_MONTHS(SYSDATE, 1), 'mm'),
                 '3',
                 TRUNC(SYSDATE + 1, 'DD'),
                 '4',
                 ((SELECT SYSDATE
                     FROM DUAL
                    WHERE TO_CHAR((SYSDATE), 'dd') <
                          LPAD(B.PARA_CODE6, 2, '0')) UNION ALL
                  (SELECT TRUNC(ADD_MONTHS(SYSDATE, 1), 'mm')
                     FROM DUAL
                    WHERE TO_CHAR((SYSDATE), 'dd') >
                          LPAD(B.PARA_CODE6, 2, '0'))),
                 SYSDATE),
          TO_DATE('2050-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS')
     FROM TF_B_TRADE_PREDEAL A, TD_S_COMMPARA B, TD_M_SP_BIZ C
    WHERE A.TRADE_ID = :TRADE_ID
      AND B.PARAM_ATTR = 908
      AND B.PARAM_CODE = :SP_CODE
      AND B.PARA_CODE1 = :BIZ_CODE
      AND B.PARA_CODE2 = '1'
      AND (B.EPARCHY_CODE = A.TRADE_EPARCHY_CODE OR B.EPARCHY_CODE = 'ZZZZ')
      AND C.SP_CODE = B.PARAM_CODE
      AND C.BIZ_CODE = B.PARA_CODE1
      AND EXISTS
    (SELECT 1 FROM TD_B_DISCNT WHERE DISCNT_CODE = B.PARA_CODE5)
      AND NOT EXISTS
    (SELECT 1
             FROM TF_B_TRADE_DISCNT
            WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
              AND ACCEPT_MONTH = TO_NUMBER(SUBSTRB(:TRADE_ID, 5, 2))
              AND DISCNT_CODE = B.PARA_CODE5
              AND MODIFY_TAG = '0'))