UPDATE Ti_b_User_Specialepay a SET a.modify_tag = '2'
        WHERE EXISTS(SELECT 1 FROM ( SELECT TRADE_ID, PARTITION_ID,USER_ID, USER_ID_A, ACCT_ID, ACCT_ID_B, PAYITEM_CODE, START_CYCLE_ID, END_CYCLE_ID, BIND_TYPE, LIMIT_TYPE, LIMIT, COMPLEMENT_TAG, RSRV_STR1, RSRV_STR2, RSRV_STR3, UPDATE_STAFF_ID, UPDATE_DEPART_ID, UPDATE_TIME
                                     FROM TI_B_USER_SPECIALEPAY
                                     WHERE sync_sequence = to_number(:SYNC_SEQUENCE)
                                     AND modify_tag = '9'
                                     MINUS
                                     SELECT TRADE_ID,PARTITION_ID, USER_ID, USER_ID_A, ACCT_ID, ACCT_ID_B, PAYITEM_CODE, START_CYCLE_ID, END_CYCLE_ID, BIND_TYPE, LIMIT_TYPE, LIMIT, COMPLEMENT_TAG, RSRV_STR1, RSRV_STR2, RSRV_STR3, UPDATE_STAFF_ID, UPDATE_DEPART_ID, UPDATE_TIME
                                     FROM TF_B_TRADE_SPECIALEPAY_BAK
                                     WHERE trade_id = to_number(:TRADE_ID)
                                     AND accept_month = :ACCEPT_MONTH ) t1
                     WHERE a.user_id = t1.user_id
                     AND a.acct_id = t1.acct_id
                     AND a.payitem_code = t1.payitem_code
                     AND a.start_cycle_id = t1.start_cycle_id)
        AND sync_sequence = to_number(:SYNC_SEQUENCE)
        AND modify_tag = '9'