DELETE tf_b_trade_attr t
where 1=1 and exists(select 1 from tf_b_trade_discnt d where d.trade_id=:TRADE_ID and d.discnt_code=:DISCNT_CODE and d.user_id=:USER_ID 
and d.inst_id=t.inst_id and d.rsrv_tag1='2')