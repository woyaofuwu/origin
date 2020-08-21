select count(1) recordcount from tf_b_trade_discnt a
where trade_id=:TRADE_ID and modify_tag='0'
and not exists (select 1 from td_b_discnt b,tf_b_trade c where
a.trade_id=c.trade_id and c.user_id=a.id and b.discnt_code=a.discnt_code and
(upper(nvl(b.rsrv_str2,'ZZZZ'))='ZZZZ' or b.rsrv_str2=c.eparchy_code) )