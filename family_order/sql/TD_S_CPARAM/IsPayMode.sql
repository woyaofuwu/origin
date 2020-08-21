select count(1) recordcount
 from tf_f_account a where a.pay_mode_code=:PAY_MODE_CODE
 and acct_id =
(SELECT acct_id
  FROM tf_a_payrelation
 WHERE user_id=to_number(:USER_ID)
   AND partition_id=MOD(to_number(:USER_ID),10000)
   AND default_tag='1'
   AND act_tag='1'
   AND (select acyc_id from TD_A_ACYCPARA
        where acyc_start_time <= SYSDATE and acyc_end_time >= SYSDATE)
   between  start_acyc_id and end_acyc_id)