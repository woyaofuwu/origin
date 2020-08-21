select count(1) recordcount from tf_a_payrelation a,tf_a_payrelation b 
where a.default_tag = '1'
and b.default_tag = '1'
and a.act_tag = '1'
and b.act_tag = '1'
and a.user_id <> b.user_id
and a.acct_id = b.acct_id
and a.user_id = TO_NUMBER(:USER_ID)
and a.PARTITION_ID=MOD(TO_NUMBER(:USER_ID), 10000)
and TO_CHAR(SYSDATE,'YYYYMM') BETWEEN  a.start_cycle_id and a.end_cycle_id
and TO_CHAR(SYSDATE,'YYYYMM') BETWEEN  b.start_cycle_id and b.end_cycle_id
and rownum < 2