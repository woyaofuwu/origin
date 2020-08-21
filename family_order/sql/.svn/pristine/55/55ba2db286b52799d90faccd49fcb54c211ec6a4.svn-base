UPDATE tf_a_payrelation
   SET act_tag = '1',end_cycle_id = to_number(:END_CYCLE_ID),update_time = SYSDATE,update_staff_id = :UPDATE_STAFF_ID,update_depart_id = :UPDATE_DEPART_ID
 WHERE user_id = :USER_ID and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND act_tag = '1'
   AND end_cycle_id >= to_number(:END_CYCLE_ID)