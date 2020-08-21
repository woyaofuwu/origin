UPDATE TI_B_USER_SVCSTATE a SET a.modify_tag = '2'
        WHERE EXISTS(SELECT 1 FROM ( SELECT USER_ID,MAIN_TAG,SERVICE_ID,STATE_CODE,START_DATE,END_DATE
                                     FROM TI_B_USER_SVCSTATE
                                     WHERE sync_sequence = to_number(:SYNC_SEQUENCE)
                                     AND modify_tag = '9'
                                     MINUS
                                     SELECT USER_ID,MAIN_TAG,SERVICE_ID,STATE_CODE,START_DATE,END_DATE
                                     FROM TF_B_TRADE_SVCSTATE_BAK
                                     WHERE trade_id = to_number(:TRADE_ID)
                                     AND accept_month = :ACCEPT_MONTH ) t1
                     WHERE a.user_id = t1.user_id
                       AND a.service_id = t1.service_id
                       AND a.state_code = t1.state_code
                       AND a.start_date = t1.start_date)
        AND sync_sequence = to_number(:SYNC_SEQUENCE)
        AND modify_tag = '9'