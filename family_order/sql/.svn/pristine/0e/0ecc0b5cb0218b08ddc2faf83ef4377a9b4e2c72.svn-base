INSERT INTO tf_b_trade_payrelation(trade_id,accept_month,user_id,acct_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,limit_type,limit,complement_tag) 
SELECT to_number(:TRADE_ID),substrb(:TRADE_ID,5,2),:RSRV_STR6,a.acct_id,
       :PAYITEM_CODE,0,0,'0',b.acyc_id,481,:DEFAULT_TAG,'0',0,'0'
  FROM tf_a_payrelation a,TD_A_ACYCPARA b
 WHERE a.user_id=to_number(:USER_ID)
   AND a.partition_id=MOD(to_number(:USER_ID),10000)
   AND a.default_tag='1'
   AND a.act_tag='1'
   AND b.acyc_start_time <= sysdate AND sysdate < b.acyc_end_time
   AND b.acyc_id between  a.start_cycle_id and a.end_cycle_id