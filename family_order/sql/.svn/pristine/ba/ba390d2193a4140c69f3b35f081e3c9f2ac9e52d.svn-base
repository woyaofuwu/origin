select count(1) recordcount from tf_a_payrelation a ,tf_f_account_consign b
where 1=1
and a.acct_id = b.acct_id 
and a.act_tag = '1'
and a.default_tag = '1'
and b.pay_mode_code = '1'
and a.user_id = TO_NUMBER(:USER_ID)
and a.PARTITION_ID=MOD(TO_NUMBER(:USER_ID), 10000)
and TO_CHAR(SYSDATE,'YYYYMM') BETWEEN  a.start_cycle_id and a.end_cycle_id
and rownum < 2