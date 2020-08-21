SELECT COUNT(1) RECORDCOUNT
  FROM TF_B_TRADE_SVC A
 WHERE A.MODIFY_TAG = :CUR_MODIFY_TAG
   AND A.USER_ID = :USER_ID
   AND A.TRADE_ID = :TRADE_ID
   AND A.SERVICE_ID IN (SELECT C.SERVICE_ID
                          FROM TF_B_TRADE B, TF_B_TRADE_SVC C
                         WHERE B.TRADE_ID = C.TRADE_ID
                           AND C.MODIFY_TAG = :MODIFY_TAG
                           AND B.USER_ID = :USER_ID
                           AND B.EXEC_TIME > SYSDATE)