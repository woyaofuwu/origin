UPDATE TI_B_USER_ATTR t SET t.modify_tag = '2'
        WHERE EXISTS (SELECT 1 FROM (SELECT PARTITION_ID,USER_ID,INST_TYPE,INST_ID,ATTR_CODE,ATTR_VALUE,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
                               FROM TI_B_USER_ATTR
                               WHERE sync_sequence = to_number(:SYNC_SEQUENCE)
                               AND MODIFY_TAG='9'
                             MINUS
                             SELECT PARTITION_ID,USER_ID,INST_TYPE,INST_ID,ATTR_CODE,ATTR_VALUE,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
                               FROM TF_B_TRADE_ATTR_BAK
                               WHERE TRADE_ID=:TRADE_ID
                               AND accept_month=:ACCEPT_MONTH ) b
               WHERE b.user_id = t.user_id
               AND b.inst_type=t.inst_type
               AND b.inst_id = t.inst_id
               AND b.attr_code=t.attr_code
               AND b.start_date=t.start_date)
       AND sync_sequence = to_number(:SYNC_SEQUENCE)
       AND modify_tag = '9'