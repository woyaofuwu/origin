UPDATE tf_a_payrelation
  SET end_cycle_id=(select acyc_id-1 from td_a_acycpara a
     where sysdate between a.ACYC_START_TIME and ACYC_END_TIME ),
      update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE   
 WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND payitem_code=:PAYITEM_CODE
   AND default_tag=:DEFAULT_TAG
   AND act_tag=:ACT_TAG
   AND start_cycle_id <= :END_CYCLE_ID
   AND end_cycle_id >= :END_CYCLE_ID