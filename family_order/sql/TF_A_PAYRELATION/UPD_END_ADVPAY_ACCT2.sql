UPDATE tf_a_payrelation
   SET end_cycle_id     = :END_CYCLE_ID,
       update_staff_id  = :UPDATE_STAFF_ID,
       update_depart_id = :UPDATE_DEPART_ID,
       update_time      = SYSDATE
 WHERE acct_id = TO_NUMBER(:ACCT_ID)
   AND end_cycle_id >= :NEXT_END_CYCLE_ID
   AND default_tag = '0'
   AND act_tag = '1'