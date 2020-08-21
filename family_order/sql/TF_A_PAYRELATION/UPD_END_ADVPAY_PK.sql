UPDATE tf_a_payrelation
   SET end_cycle_id=:END_CYCLE_ID,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND payitem_code=:PAYITEM_CODE
   AND end_cycle_id >= :START_CYCLE_ID
   AND act_tag=:ACT_TAG