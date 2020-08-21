UPDATE TI_B_USER_SPECIALEPAY t SET t.modify_tag = '0'
         WHERE NOT EXISTS (SELECT 1 FROM TF_B_TRADE_SPECIALEPAY_BAK b
                           WHERE trade_id=:TRADE_ID
                           AND accept_month=:ACCEPT_MONTH
                           AND t.user_id=b.user_id
                           AND t.ACCT_ID=b.ACCT_ID
                           AND t.PAYITEM_CODE=b.PAYITEM_CODE
                           AND t.START_CYCLE_ID = b.START_CYCLE_ID )
         AND t.sync_sequence = to_number(:SYNC_SEQUENCE)
         AND modify_tag = '9'