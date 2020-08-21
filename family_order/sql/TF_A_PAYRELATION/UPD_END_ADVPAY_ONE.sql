UPDATE tf_a_payrelation
   SET end_cycle_id=to_char(add_months(to_date(:END_CYCLE_ID,'yyyymmdd'),-1),'yyyymmdd'),act_tag='0',update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=SYSDATE 
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND payitem_code=:PAYITEM_CODE
   AND TO_CHAR(SYSDATE,'YYYYMMDD') <= end_cycle_id
   AND default_tag='0'
   AND act_tag='1'