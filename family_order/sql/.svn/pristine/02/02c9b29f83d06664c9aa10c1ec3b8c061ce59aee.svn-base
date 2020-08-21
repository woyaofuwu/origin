UPDATE tf_a_payrelation
   SET acct_id=TO_NUMBER(:ACCT_ID),update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE   
 WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND start_cycle_id >= TO_CHAR(SYSDATE,'YYYYMMDD')
   AND default_tag=:DEFAULT_TAG
   AND act_tag=:ACT_TAG