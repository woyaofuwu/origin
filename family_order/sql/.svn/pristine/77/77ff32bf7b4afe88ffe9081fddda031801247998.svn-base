DELETE FROM tf_a_payrelation
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND payitem_code=:PAYITEM_CODE
   AND start_cycle_id=:START_CYCLE_ID