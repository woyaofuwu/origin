SELECT C.TRADE_ID
 from tf_b_trade c,tf_b_trade_platsvc d 
where  c.trade_id= d.trade_id 
and    d.accept_month=substr(c.trade_id,5,2)
and    c.user_id=d.user_id
and    c.trade_type_code=3788
and    c.accept_date >sysdate -3
and    c.user_id =:USER_ID
and    d.user_id =:USER_ID
and    c.trade_id <> :TRADE_ID
and  exists (select 1 from 
					       (select b.sp_code,b.biz_code from tf_b_trade a,tf_b_trade_platsvc b
															where b.accept_month=substr(a.trade_id,5,2)
															and   a.accept_date > sysdate-3
															and   b.user_id=a.user_id
															and   a.trade_type_code=3788
															and   a.trade_id=b.trade_id 
                              and   a.trade_id =:TRADE_ID
															and   a.user_id =:USER_ID
															and   b.user_id =:USER_ID ) f
		where  d.sp_code=f.sp_code
		and    d.biz_code=f.biz_code )