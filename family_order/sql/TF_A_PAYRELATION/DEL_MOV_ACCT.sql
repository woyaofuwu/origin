DELETE FROM tf_a_payrelation
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND start_cycle_id > end_cycle_id