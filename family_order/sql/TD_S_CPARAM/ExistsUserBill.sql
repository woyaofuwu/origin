SELECT COUNT(1) recordcount
FROM ts_a_bill a
WHERE exists(select 1 from tf_a_payrelation
             where USER_ID=:USER_ID
              and partition_id=mod(:USER_ID,10000)
              and acct_id=a.acct_id
              and mod(acct_id,10000)=a.partition_id
              and user_id=a.user_id)
AND acyc_id < (select acyc_id from TD_A_ACYCPARA
							where sysdate between acyc_start_time and acyc_end_time)