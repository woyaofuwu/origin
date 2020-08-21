select COUNT(1)  recordcount
 from tf_b_trade a,tf_a_payrelation b
 where a.user_id = b.user_id
 AND b.partition_id = MOD(a.user_id,10000)
 AND a.trade_id = to_number(:TRADE_ID)
 AND a.acct_id = b.acct_id
 and (select acyc_id
        from td_a_acycpara
       where sysdate between acyc_start_time and acyc_end_time) BETWEEN b.start_acyc_id and b.end_acyc_id
 and b.act_tag='1'
 AND b.payitem_code = '-1'
 AND b.default_tag ='1'