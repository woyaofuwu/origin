UPDATE TI_B_USER_IMPU t SET t.modify_tag = '2'
        WHERE EXISTS (SELECT 1 FROM (SELECT TRADE_ID,PARTITION_ID,USER_ID,TEL_URL,SIP_URL,IMPI,IMS_USER_ID,IMS_PASSWORD,START_DATE,END_DATE,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5
                               FROM TI_B_USER_IMPU
                               WHERE sync_sequence = to_number(:SYNC_SEQUENCE)
                               AND MODIFY_TAG='9'
                             MINUS
                             SELECT TRADE_ID,PARTITION_ID,USER_ID,TEL_URL,SIP_URL,IMPI,IMS_USER_ID,IMS_PASSWORD,START_DATE,END_DATE,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5
                               FROM TF_B_TRADE_IMPU_BAK
                               WHERE TRADE_ID=:TRADE_ID
                               AND accept_month=:ACCEPT_MONTH ) b
                      WHERE t.user_id=b.user_id
                      AND t.start_date=b.start_date)
       AND sync_sequence = to_number(:SYNC_SEQUENCE)
       AND modify_tag = '9'