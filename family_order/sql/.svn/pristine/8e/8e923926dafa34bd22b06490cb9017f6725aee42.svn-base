UPDATE tf_a_payrelation
   SET end_cycle_id=:END_CYCLE_ID,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE  
 WHERE partition_id=MOD(:USER_ID, 10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND default_tag='1'
   AND act_tag='1'
   AND to_number(to_char(sysdate, 'yyyymmdd')) BETWEEN start_cycle_id AND end_cycle_id