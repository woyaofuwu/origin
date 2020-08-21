UPDATE TI_B_RELATION_UU t SET t.modify_tag = '0'
         WHERE NOT EXISTS (SELECT 1 FROM TF_B_TRADE_RELATION_UU_BAK b
                           WHERE trade_id=:TRADE_ID
                           AND accept_month=:ACCEPT_MONTH
                           AND t.user_id_b = b.user_id_b
                           AND t.user_id_a = b.user_id_a
                           AND t.relation_type_code = b.relation_type_code
                           AND t.start_date = b.start_date)
         AND t.sync_sequence = to_number(:SYNC_SEQUENCE)
         AND modify_tag = '9'