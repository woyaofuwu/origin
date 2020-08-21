UPDATE tf_a_payrelation
   SET acct_priority=:ACCT_PRIORITY,user_priority=:USER_PRIORITY,bind_type=:BIND_TYPE,start_cycle_id=:START_CYCLE_ID,end_cycle_id=:END_CYCLE_ID,default_tag=:DEFAULT_TAG,act_tag=:ACT_TAG,limit_type=:LIMIT_TYPE,limit=:LIMIT,complement_tag=:COMPLEMENT_TAG,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE  
 WHERE user_id=:USER_ID and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND acct_id=:ACCT_ID
   AND payitem_code=:PAYITEM_CODE
   AND act_tag = '1'