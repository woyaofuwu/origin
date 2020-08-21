SELECT a.user_id,a.acct_id,-1,0,0,'1',a.start_cycle_id,b.acyc_id,a.default_tag,'0',0,'0'
  FROM tf_a_payrelation a,td_a_acycpara b
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.acct_id = TO_NUMBER(:ACCT_ID)
   AND a.act_tag = '1'
   AND sysdate BETWEEN b.acyc_start_time AND b.acyc_end_time
   AND b.acyc_id BETWEEN a.start_cycle_id AND a.end_cycle_id
   AND rownum<2