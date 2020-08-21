UPDATE TI_B_SALE_ACTIVE t SET t.modify_tag = '0'
         WHERE NOT EXISTS (SELECT 1 FROM TF_B_TRADE_SALE_ACTIVE_BAK b
                           WHERE trade_id=:TRADE_ID
                           AND accept_month=:ACCEPT_MONTH
                           AND t.user_id=b.user_id
                           AND t.package_id = package_id
                           AND t.product_id = product_id
                           AND t.campn_id = campn_id
                           AND t.relation_trade_id = relation_trade_id
                           AND t.start_date = start_date )
         AND t.sync_sequence = to_number(:SYNC_SEQUENCE)
         AND modify_tag = '9'