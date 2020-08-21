UPDATE tf_a_payrelation
   SET end_cycle_id=decode(sign(:END_CYCLE_ID-481),1,481,:END_CYCLE_ID),default_tag=:DEFAULT_TAG,act_tag=:ACT_TAG,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=sysdate  
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND payitem_code=:PAYITEM_CODE
   AND start_cycle_id=:START_CYCLE_ID