UPDATE TI_B_ACCOUNT t SET t.modify_tag = '0'
         WHERE NOT EXISTS (SELECT 1 FROM TF_B_TRADE_ACCOUNT_BAK b
                           WHERE trade_id=:TRADE_ID
                           AND accept_month=:ACCEPT_MONTH
                           AND t.acct_id=b.acct_id)
         AND t.sync_sequence = to_number(:SYNC_SEQUENCE)
         AND modify_tag = '9'