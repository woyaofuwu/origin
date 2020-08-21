UPDATE tf_a_payrelation
   SET  end_cycle_id=to_char(to_date(:END_CYCLE_ID,'yyyymmdd')-1,'yyyymmdd'),
        act_tag='1',
        update_staff_id=:UPDATE_STAFF_ID,
        update_depart_id=:UPDATE_DEPART_ID,
        update_time=SYSDATE  
 WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND default_tag=:DEFAULT_TAG
   AND act_tag=:ACT_TAG
   AND end_cycle_id >= :END_CYCLE_ID