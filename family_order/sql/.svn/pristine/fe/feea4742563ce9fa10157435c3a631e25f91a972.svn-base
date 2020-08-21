INSERT INTO tf_b_trade_payrelation(trade_id,accept_month,user_id,acct_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,limit_type,limit,complement_tag)
SELECT to_number(:TRADE_ID),substrb(:TRADE_ID,5,2),a.user_id,a.acct_id,
       a.payitem_code,a.acct_priority,a.user_priority,a.bind_type,a.start_cycle_id,b.acyc_id,
       a.default_tag,a.limit_type,a.limit,a.complement_tag 
  FROM tf_a_payrelation a,TD_A_ACYCPARA b
 WHERE a.user_id=to_number(:USER_ID)
   AND a.partition_id=MOD(to_number(:USER_ID),10000)
   AND a.acct_id=:ACCT_ID
   AND a.act_tag='1'
   AND b.acyc_start_time <= sysdate AND sysdate < b.acyc_end_time
   AND b.acyc_id between  a.start_cycle_id and a.end_cycle_id