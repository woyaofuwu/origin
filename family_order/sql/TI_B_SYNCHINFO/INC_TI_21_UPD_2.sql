UPDATE TI_B_USER_RES a SET a.modify_tag = '2'
        WHERE EXISTS(SELECT 1 FROM ( SELECT PARTITION_ID,USER_ID,USER_ID_A,RES_TYPE_CODE,RES_CODE,IMSI,KI,INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
                                     FROM TI_B_USER_RES
                                     WHERE sync_sequence = to_number(:SYNC_SEQUENCE)
                                     AND modify_tag = '9'
                                     MINUS
                                     SELECT PARTITION_ID,USER_ID,USER_ID_A,RES_TYPE_CODE,RES_CODE,IMSI,KI,INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
                                     FROM TF_B_TRADE_RES_BAK
                                     WHERE trade_id = to_number(:TRADE_ID)
                                     AND accept_month = :ACCEPT_MONTH ) t1
                     WHERE a.user_id = t1.user_id
                       AND a.user_id_a = t1.user_id_a
                       AND a.res_type_code = t1.res_type_code
                       AND a.res_code = t1.res_code
                       AND a.start_date = t1.start_date)
        AND sync_sequence = to_number(:SYNC_SEQUENCE)
        AND modify_tag = '9'