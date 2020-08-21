SELECT DECODE(COUNT(DISTINCT SERVICE_ID), 0, 1, 0) RECORDCOUNT
  FROM (SELECT SERVICE_ID
          FROM TF_F_USER_SVC A
         WHERE USER_ID = TO_NUMBER(:USER_ID)
           AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID),
                                  10000)
           AND END_DATE > SYSDATE
           AND NOT EXISTS
         (SELECT 1
                  FROM TF_B_TRADE_SVC
                 WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
                   AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
                   AND MODIFY_TAG = '1'
                   AND SERVICE_ID = A.SERVICE_ID
                   AND START_DATE = A.START_DATE)
           AND NOT EXISTS
         (SELECT 1
                  FROM TF_B_TRADE_SVC
                 WHERE TRADE_ID IN (SELECT TRADE_ID
                                      FROM TF_B_TRADE
                                     WHERE USER_ID = TO_NUMBER(:USER_ID)
                                       AND CANCEL_TAG = '0')
                   AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
                   AND MODIFY_TAG = '1'
                   AND SERVICE_ID = A.SERVICE_ID
                   AND START_DATE = A.START_DATE)
        UNION ALL
        SELECT SERVICE_ID
          FROM TF_B_TRADE_SVC
         WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)
           AND USER_ID = TO_NUMBER(:USER_ID)
           AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
           AND MODIFY_TAG = '0'
        UNION ALL
        SELECT SERVICE_ID
          FROM TF_B_TRADE_SVC
         WHERE TRADE_ID IN (SELECT TRADE_ID
                              FROM TF_B_TRADE
                             WHERE USER_ID = TO_NUMBER(:USER_ID)
                               AND CANCEL_TAG = '0')
           AND USER_ID = TO_NUMBER(:USER_ID)
           AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
           AND MODIFY_TAG = '0') VA
 WHERE SERVICE_ID IN (17, 18, 19,99010001)