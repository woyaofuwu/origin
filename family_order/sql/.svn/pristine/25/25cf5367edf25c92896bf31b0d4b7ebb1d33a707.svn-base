UPDATE TI_B_USER_RES t SET t.modify_tag = '0'
         WHERE NOT EXISTS (SELECT 1 FROM TF_B_TRADE_RES_BAK b
                           WHERE trade_id=:TRADE_ID
                           AND accept_month=:ACCEPT_MONTH
                           AND t.user_id=b.user_id
                           AND t.user_id_a = b.user_id_a
                           AND t.res_type_code = b.res_type_code
                           AND t.res_code = b.res_code
                           AND t.start_date = b.start_date )
         AND t.sync_sequence = to_number(:SYNC_SEQUENCE)
         AND modify_tag = '9'