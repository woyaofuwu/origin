INSERT INTO TI_B_RELATION_UU (SYNC_SEQUENCE,MODIFY_TAG,TRADE_ID,PARTITION_ID,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3)
 SELECT /*+index(a)*/TO_NUMBER(:SYNC_SEQUENCE),:MODIFY_TAG,TO_NUMBER(:TRADE_ID),mod(user_id_b,10000),USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3
 FROM tf_f_relation_uu a
 WHERE USER_ID_A in ( select user_id_a
                      from tf_b_trade_relation t1,td_s_relation t2
                      where trade_id = TO_NUMBER(:TRADE_ID)
                      and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                      and t1.role_code_b='1'
                     and t1.relation_type_code = t2.relation_type_code
                     and t2.relation_kind='F') --亲情                    
 AND NOT EXISTS (select 1 from TI_B_RELATION_UU c
                 where c.sync_sequence=TO_NUMBER(:SYNC_SEQUENCE)
                 and a.partition_id=c.partition_id
                 and a.user_id_a=c.user_id_a
                 and a.user_id_b=c.user_id_b
                 and a.relation_type_code=c.relation_type_code
                 and a.start_date=c.start_date
                 )