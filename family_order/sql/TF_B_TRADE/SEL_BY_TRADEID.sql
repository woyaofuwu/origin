select a.* from tf_B_trade a where a.trade_id=:TRADE_ID
union
select b.*  from tf_bh_trade b where b.trade_id=:TRADE_ID