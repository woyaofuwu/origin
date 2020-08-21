UPDATE tf_a_payrelation
   SET act_tag='0',update_time=SYSDATE  
 WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND payitem_code=:PAYITEM_CODE
   AND start_cycle_id=:START_CYCLE_ID
   AND end_cycle_id=:END_CYCLE_ID