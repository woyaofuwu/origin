UPDATE TF_F_ACCT_DISCNT T
   SET T.END_DATE    = (SELECT END_DATE
                          FROM TF_B_TRADE_ACCT_DISCNT T3
                         WHERE T3.TRADE_ID = :TRADE_ID
                           AND T3.MODIFY_TAG = '1'
                           AND T3.ACCT_ID = T.ACCT_ID
                           AND T3.INST_ID = T.INST_ID),
       T.UPDATE_TIME = SYSDATE
 WHERE EXISTS (SELECT 1
          FROM TF_B_TRADE_ACCT_DISCNT T2
         WHERE T2.TRADE_ID = :TRADE_ID
           AND T2.MODIFY_TAG = '1'
           AND T2.ACCT_ID = T.ACCT_ID
           AND T2.INST_ID = T.INST_ID)
   AND T.END_DATE > SYSDATE