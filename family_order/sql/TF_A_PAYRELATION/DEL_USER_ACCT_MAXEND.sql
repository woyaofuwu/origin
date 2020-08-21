DELETE FROM tf_a_payrelation
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID), 10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND default_tag=:DEFAULT_TAG
   AND act_tag=:ACT_TAG
   AND end_cycle_id=(SELECT MAX(end_cycle_id)
                      FROM tf_a_payrelation 
                     WHERE partition_id=MOD(TO_NUMBER(:USER_ID), 10000)
                       AND user_id=TO_NUMBER(:USER_ID)
                       AND acct_id=TO_NUMBER(:ACCT_ID)
                       AND default_tag=:DEFAULT_TAG
                       AND act_tag=:ACT_TAG)