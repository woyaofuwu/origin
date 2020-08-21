UPDATE tf_a_payrelation SET end_cycle_id=:CUR_ACYC_ID-2
WHERE partition_id = mod(TO_NUMBER(:USER_ID),10000)
AND user_id= :USER_ID
AND acct_id= :ACCT_ID
AND default_tag= :DEFAULT_TAG
AND act_tag= :ACT_TAG
AND end_cycle_id < :CUR_ACYC_ID