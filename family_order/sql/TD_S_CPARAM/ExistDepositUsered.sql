Select COUNT(*) recordcount
From tf_f_accountdeposit a
Where a.acct_id=(Select acct_id From tf_a_payrelation Where user_id=:USER_ID And default_tag='1'
And end_acyc_id >=(Select Min(acyc_id) From td_a_acycpara Where acyc_end_time>Sysdate))
And a.deposit_code in(29,11)
And (a.money=0
Or a.money < ( Select nvl(b.real_fee,0) From tf_o_leaverealfee b Where b.user_id=:USER_ID )
)