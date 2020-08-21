SELECT 1 FROM TD_B_QRY_RULE_CONFIG V, TF_F_USER_DISCNT W
                           WHERE V.ID_TYPE = 'D'
                             AND V.ID = W.DISCNT_CODE
                             AND W.END_DATE > SYSDATE
                             AND W.USER_ID = :USER_ID
                             AND W.PARTITION_ID = MOD(:USER_ID, '10000')
                             AND (V.SERV_TYPE = 'S' OR V.SERV_TYPE='Z')
                             AND V.SERVICE_ID = :SERVICE_ID
                             AND (V.EPARCHY_CODE = :EPARCHY_CODE OR V.EPARCHY_CODE = 'ZZZZ')
                             AND SYSDATE BETWEEN V.START_DATE AND V.END_DATE