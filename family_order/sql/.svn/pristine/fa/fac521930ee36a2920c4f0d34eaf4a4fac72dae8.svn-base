SELECT PACKAGE_ID PRODUCT_CODE,
       'null' AS PACKAGE_ID,
       PRODUCT_INST_ID,
       MIN(START_DATE) START_DATE,
       MAX(END_DATE) END_DATE,
       SERIAL_NUMBER,
       CARDTYPE,
       CARDPHYSICALTYPE,
       MODIFY_TAG,
       IMSI,
       KI,
       SUBS_ID,
       OPCKEYID
  FROM (SELECT B.PARA_CODE2 PACKAGE_ID,
               C.PROD_INST_ID PRODUCT_INST_ID,
               TO_CHAR(A.START_DATE, 'YYYYMMDDHH24MMSS') START_DATE,
               TO_CHAR(A.END_DATE, 'YYYYMMDDHH24MMSS') END_DATE,
               D.SERIAL_NUMBER,
               SUBSTR(E.RSRV_STR1, 0, INSTR(E.RSRV_STR1, '|') - 1) CARDTYPE,
               E.RSRV_STR5 CARDPHYSICALTYPE,
               A.MODIFY_TAG,
               E.IMSI,
               E.KI,
               G.SUBS_ID,
               F.OPC_CIPHER OPCKEYID,
               C.RSRV_STR1
          FROM TF_B_TRADE_SVC   A,
               TD_S_COMMPARA    B,
               TF_F_INSTANCE_PF C,
               TF_B_TRADE       D,
               TF_F_USER_RES    E,
               TI_IOT_CARDDATAFILE   F,
               TF_F_INSTANCE_PF G
         WHERE TO_CHAR(A.SERVICE_ID) = B.PARAM_CODE
           AND B.SUBSYS_CODE = 'CSM'
           AND B.PARAM_ATTR = '9014'
           AND A.INST_ID = C.INST_ID
           AND C.INST_TYPE = 'P'
           AND A.USER_ID = G.USER_ID
           AND G.INST_ID = -1
           AND F.IMSI = E.IMSI
           AND A.TRADE_ID = D.TRADE_ID
           AND A.USER_ID = E.USER_ID
           AND E.RES_TYPE_CODE = '1'
           AND A.TRADE_ID = TO_NUMBER(:TRADE_ID)
           AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
           AND A.MODIFY_TAG IN ('0', '1', '2')
           AND SYSDATE BETWEEN E.START_DATE AND E.END_DATE
        UNION
        SELECT B.PARA_CODE2 PACKAGE_ID,
               C.PROD_INST_ID PRODUCT_INST_ID,
               TO_CHAR(A.START_DATE, 'YYYYMMDDHH24MMSS') START_DATE,
               TO_CHAR(A.END_DATE, 'YYYYMMDDHH24MMSS') END_DATE,
               D.SERIAL_NUMBER,
               SUBSTR(E.RSRV_STR1, 0, INSTR(E.RSRV_STR1, '|') - 1) CARDTYPE,
               E.RSRV_STR5 CARDPHYSICALTYPE,
               A.MODIFY_TAG,
               E.IMSI,
               E.KI,
               G.SUBS_ID,
               F.OPC_CIPHER OPCKEYID,
               C.RSRV_STR1
          FROM TF_B_TRADE_DISCNT A,
               TD_S_COMMPARA     B,
               TF_F_INSTANCE_PF  C,
               TF_B_TRADE        D,
               TF_F_USER_RES     E,
               TI_IOT_CARDDATAFILE    F,
               TF_F_INSTANCE_PF  G
         WHERE TO_CHAR(A.DISCNT_CODE) = B.PARAM_CODE
           AND B.SUBSYS_CODE = 'CSM'
           AND B.PARAM_ATTR = '9013'
           AND A.INST_ID = C.INST_ID
           AND C.INST_TYPE = 'P'
           AND A.USER_ID = G.USER_ID
           AND G.INST_ID = -1
           AND A.TRADE_ID = D.TRADE_ID
           AND A.USER_ID = E.USER_ID
           AND E.RES_TYPE_CODE = '1'
           AND F.IMSI = E.IMSI
           AND A.TRADE_ID = TO_NUMBER(:TRADE_ID)
           AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
           AND A.MODIFY_TAG IN ('0', '1', '2')
           AND SYSDATE BETWEEN E.START_DATE AND E.END_DATE)
 WHERE (MODIFY_TAG = '0' AND NVL(RSRV_STR1, '0') <> 'USER_PRE_INST_ID')
    OR (MODIFY_TAG = '1' AND NOT EXISTS
        (SELECT 1
           FROM TF_B_TRADE
          WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
            AND TRADE_TYPE_CODE = '110'))
 GROUP BY PACKAGE_ID,
          PRODUCT_INST_ID,
          SERIAL_NUMBER,
          CARDTYPE,
          CARDPHYSICALTYPE,
          MODIFY_TAG,
          IMSI,
          KI,
          SUBS_ID,
          OPCKEYID