SELECT PARTITION_ID,to_char(USER_ID) USER_ID,to_char(ACCT_ID) ACCT_ID,PAYITEM_CODE,ACCT_PRIORITY, USER_PRIORITY,to_char(ADDUP_MONTHS) ADDUP_MONTHS,ADDUP_METHOD,BIND_TYPE,DEFAULT_TAG, ACT_TAG,LIMIT_TYPE,to_char(LIMIT) LIMIT,COMPLEMENT_TAG,to_char(INST_ID) INST_ID, START_CYCLE_ID,END_CYCLE_ID,to_char(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID, REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4, RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9, RSRV_STR10
  FROM tf_a_payrelation
 WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND default_tag=:DEFAULT_TAG AND act_tag='1'
   AND start_cycle_id <= end_cycle_id
   AND end_cycle_id=
   (select max(a.end_cycle_id) from tf_a_payrelation a
    where a.user_id = TO_NUMBER(:USER_ID)
      and a.partition_id=mod(TO_NUMBER(:USER_ID),10000)
      and a.default_tag = :DEFAULT_TAG AND act_tag='1'
      AND start_cycle_id <= end_cycle_id)