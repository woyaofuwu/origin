SELECT PARTITION_ID,
       to_char(USER_ID) USER_ID,
       to_char(ACCT_ID) ACCT_ID,
       PAYITEM_CODE,
       ACCT_PRIORITY,
       USER_PRIORITY,
       to_char(ADDUP_MONTHS) ADDUP_MONTHS,
       ADDUP_METHOD,
       BIND_TYPE,
       DEFAULT_TAG,
       ACT_TAG,
       LIMIT_TYPE,
       to_char(LIMIT) LIMIT,
       COMPLEMENT_TAG,
       to_char(INST_ID) INST_ID,
       START_CYCLE_ID,
       END_CYCLE_ID,
       to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_STR6,
       RSRV_STR7,
       RSRV_STR8,
       RSRV_STR9,
       RSRV_STR10
  FROM TF_A_PAYRELATION
 WHERE user_id = TO_NUMBER(:USER_ID)
   and partition_id = mod(TO_NUMBER(:USER_ID), 10000)
   AND default_tag = :DEFAULT_TAG
   AND act_tag = :ACT_TAG
   AND start_cycle_id < end_cycle_id
   AND end_cycle_id >= :END_CYCLE_ID