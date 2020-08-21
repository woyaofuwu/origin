--IS_CACHE=Y
SELECT 1 FROM TD_B_QRY_RULE_CONFIG X
                           WHERE X.ID_TYPE = 'B'
                             AND X.ID = :BRAND_CODE
                             AND (X.SERV_TYPE = 'S' OR X.SERV_TYPE='Z')
                             AND X.SERVICE_ID = :SERVICE_ID
                             AND (X.EPARCHY_CODE = :EPARCHY_CODE OR X.EPARCHY_CODE = 'ZZZZ')
                             AND SYSDATE BETWEEN X.START_DATE AND X.END_DATE