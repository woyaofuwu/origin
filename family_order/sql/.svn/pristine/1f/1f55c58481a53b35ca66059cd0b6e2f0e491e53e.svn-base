UPDATE ti_b_account_acctday A
   SET A.MODIFY_TAG = '0'
 WHERE NOT EXISTS (SELECT 1
          FROM tf_b_trade_account_acctday_bak B
         WHERE B.TRADE_ID = :TRADE_ID
           AND B.ACCEPT_MONTH = :ACCEPT_MONTH
           AND B.ACCT_ID = A.ACCT_ID
           and B.inst_id=A.inst_id)
   AND A.SYNC_SEQUENCE = TO_NUMBER(:SYNC_SEQUENCE)
   AND MODIFY_TAG = '9'