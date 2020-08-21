UPDATE TI_B_POSTINFO t SET t.modify_tag = '2'
        WHERE EXISTS (SELECT 1 FROM (SELECT TRADE_ID,PARTITION_ID,ID,ID_TYPE,POST_NAME,POST_TAG,POST_CONTENT,POST_TYPESET,POST_CYC,POST_ADDRESS,POST_CODE,EMAIL,FAX_NBR,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
                               FROM TI_B_POSTINFO
                               WHERE sync_sequence = to_number(:SYNC_SEQUENCE)
                               AND modify_tag='9'
                             MINUS
                             SELECT TRADE_ID,PARTITION_ID,ID,ID_TYPE,POST_NAME,POST_TAG,POST_CONTENT,POST_TYPESET,POST_CYC,POST_ADDRESS,POST_CODE,EMAIL,FAX_NBR,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
                               FROM TF_B_TRADE_POSTINFO_BAK
                               WHERE trade_id=:TRADE_ID
                               AND accept_month=:ACCEPT_MONTH ) b
               WHERE b.trade_id = t.trade_id
               AND b.id = t.id
               AND b.id_type = t.id_type
               and b.start_date = t.start_date
               AND b.partition_id = t.partition_id )
       AND sync_sequence = to_number(:SYNC_SEQUENCE)
       AND modify_tag = '9'