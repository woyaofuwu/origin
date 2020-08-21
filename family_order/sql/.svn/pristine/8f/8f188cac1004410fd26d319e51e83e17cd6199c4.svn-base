UPDATE TI_B_USER_ATTR t SET t.modify_tag = '0'
         WHERE NOT EXISTS (SELECT 1 FROM TF_B_TRADE_ATTR_BAK b
                           WHERE trade_id=:TRADE_ID
                           AND accept_month=:ACCEPT_MONTH
                           AND t.user_id=b.user_id
                           AND t.inst_type=b.inst_type
                           AND t.inst_id = b.inst_id
                           AND t.attr_code=b.attr_code
                           AND t.start_date=b.start_date)
         AND t.sync_sequence = to_number(:SYNC_SEQUENCE)
         AND modify_tag = '9'