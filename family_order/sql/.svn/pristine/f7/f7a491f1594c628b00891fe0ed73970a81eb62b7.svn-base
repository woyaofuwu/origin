INSERT INTO TI_B_USER_ATTR (SYNC_SEQUENCE,MODIFY_TAG,TRADE_ID,PARTITION_ID,USER_ID,INST_TYPE,INST_ID,ATTR_CODE,ATTR_VALUE,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 )
        SELECT :SYNC_SEQUENCE,:MODIFY_TAG,:TRADE_ID, PARTITION_ID,USER_ID,INST_TYPE,INST_ID,ATTR_CODE,ATTR_VALUE,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
               FROM tf_f_user_attr a
                     WHERE user_id IN (
                     SELECT b.user_id_a FROM tf_b_trade_relation b WHERE b.trade_id = TO_NUMBER(:TRADE_ID)
                     UNION 
                     SELECT c.user_id_b FROM tf_b_trade_relation c WHERE c.trade_id = TO_NUMBER(:TRADE_ID)
                     )
                     AND NOT EXISTS (SELECT 1 FROM TI_B_USER_ATTR 
                              WHERE sync_sequence = TO_NUMBER(:SYNC_SEQUENCE)                         
                               AND a.partition_id = PARTITION_ID
                               AND a.user_id = USER_ID
                               AND a.INST_TYPE = INST_TYPE
                               AND a.INST_ID =INST_ID
                               AND a.ATTR_CODE = ATTR_CODE
                               AND a.START_DATE=START_DATE
                               )