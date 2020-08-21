SELECT TO_CHAR(T.ACCEPT_DATE, 'yyyy-mm-dd') ACCEPT_DATE,
       T.TRADE_TYPE_CODE,
       T.IN_MODE_CODE,
       T.TRADE_STAFF_ID
  FROM TF_BH_TRADE T
 WHERE 1 = 1
   AND T.TRADE_ID = (SELECT TO_CHAR(MAX(TRADE_ID)) TRADE_ID
                       FROM TF_BH_TRADE
                      WHERE 1 = 1
                        AND USER_ID = :USER_ID
                        AND (TRADE_TYPE_CODE = :TRADE_TYPE_CODE OR
                            :TRADE_TYPE_CODE IS NULL)
                        AND (CANCEL_TAG = :CANCEL_TAG OR :CANCEL_TAG IS NULL)
                        AND MONTHS_BETWEEN(SYSDATE, ACCEPT_DATE) < 7
                        AND TRADE_TYPE_CODE <> '2101')