INSERT INTO TF_B_TRADE_PAYRELATION_BAK (TRADE_ID,ACCEPT_MONTH,PARTITION_ID,USER_ID,ACCT_ID,PAYITEM_CODE,ACCT_PRIORITY,USER_PRIORITY,ADDUP_MONTHS,ADDUP_METHOD,BIND_TYPE,DEFAULT_TAG,ACT_TAG,LIMIT_TYPE,LIMIT,COMPLEMENT_TAG,INST_ID,START_CYCLE_ID,END_CYCLE_ID,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10)
 SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)),PARTITION_ID,USER_ID,ACCT_ID,PAYITEM_CODE,ACCT_PRIORITY,USER_PRIORITY,ADDUP_MONTHS,ADDUP_METHOD,BIND_TYPE,DEFAULT_TAG,ACT_TAG,LIMIT_TYPE,LIMIT,COMPLEMENT_TAG,INST_ID,START_CYCLE_ID,END_CYCLE_ID,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10
 FROM TF_A_PAYRELATION a
 WHERE (PARTITION_ID, USER_ID) IN
     ( SELECT mod(t1.USER_ID, 10000), t1.USER_ID
                      from TF_B_TRADE_PAYRELATION t1
                      where t1.trade_id = TO_NUMBER(:TRADE_ID)
                      and t1.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                      )
 AND NOT EXISTS (select 1 from TF_B_TRADE_PAYRELATION_BAK c
                 where c.trade_id = TO_NUMBER(:TRADE_ID)
                 and c.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                 and a.USER_ID=c.USER_ID
                 and a.START_CYCLE_ID=c.START_CYCLE_ID
                 )