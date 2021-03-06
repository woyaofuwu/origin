INSERT INTO TI_B_USER_DISCNT (SYNC_SEQUENCE,MODIFY_TAG,TRADE_ID,PARTITION_ID,USER_ID,USER_ID_A,PRODUCT_ID,PACKAGE_ID,DISCNT_CODE,SPEC_TAG,RELATION_TYPE_CODE,INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3)
 SELECT TO_NUMBER(:SYNC_SEQUENCE),:MODIFY_TAG,TO_NUMBER(:TRADE_ID),PARTITION_ID,USER_ID,USER_ID_A,PRODUCT_ID,PACKAGE_ID,DISCNT_CODE,SPEC_TAG,RELATION_TYPE_CODE,INST_ID,CAMPN_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
 FROM TF_F_USER_DISCNT a
 WHERE a.USER_ID_A in( select user_id_a
                     from tf_b_trade_discnt t1
                     where t1.trade_id = TO_NUMBER(:TRADE_ID)
                     and t1.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                     and t1.user_id_a<>-1 )
                      AND EXISTS (SELECT 1 FROM td_s_commpara a
                where a.subsys_code ='CSM'
                and a.param_attr = 6017
                and a.param_code =  '6'
                And  a.para_code1= relation_type_code
                and a.end_date > sysdate
                )  --亲情                   
 AND NOT EXISTS (SELECT 1 FROM ti_b_user_discnt 
                         WHERE sync_sequence = TO_NUMBER(:SYNC_SEQUENCE)
                           AND a.user_id = user_id
                           AND a.user_id_a = user_id_a
                           AND a.discnt_code = discnt_code
                           AND a.start_date = start_date)