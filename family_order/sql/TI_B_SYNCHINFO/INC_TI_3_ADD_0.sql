UPDATE TI_B_USER_DISCNT t SET t.modify_tag = '0'
         WHERE NOT EXISTS (SELECT 1 FROM TF_B_TRADE_DISCNT_BAK b
                           WHERE trade_id=:TRADE_ID
                           AND accept_month=:ACCEPT_MONTH
                           AND t.user_id=b.user_id
                           AND t.user_id_a = user_id_a
                           AND t.discnt_code = discnt_code
                           AND t.inst_id = inst_id )
         AND t.sync_sequence = to_number(:SYNC_SEQUENCE)
         AND modify_tag = '9'