UPDATE TI_B_POSTINFO t SET t.modify_tag = '0'
         WHERE NOT EXISTS (SELECT 1 FROM TF_B_TRADE_POSTINFO_BAK b
                           WHERE trade_id=:TRADE_ID
                           AND accept_month=:ACCEPT_MONTH
                           AND b.id=t.id
                           and b.id_type=t.id_type
                           and b.start_date=t.start_date)
         AND t.sync_sequence = to_number(:SYNC_SEQUENCE)
         AND modify_tag = '9'