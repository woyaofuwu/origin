--IS_CACHE=Y
SELECT 1 FROM TD_B_QRY_RULE_CONFIG U
                           WHERE U.ID_TYPE = 'P'
                             AND U.ID = :PRODUCT_ID
                             AND (U.SERV_TYPE = 'S' OR U.SERV_TYPE='Z')
                             AND U.SERVICE_ID = :SERVICE_ID
                             AND (U.EPARCHY_CODE = :EPARCHY_CODE OR U.EPARCHY_CODE = 'ZZZZ')
                             AND SYSDATE BETWEEN U.START_DATE AND U.END_DATE