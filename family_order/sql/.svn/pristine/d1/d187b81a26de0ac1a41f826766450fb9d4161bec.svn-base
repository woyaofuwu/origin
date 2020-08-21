insert into tf_b_trade_scoresub
select a.trade_id,a.accept_month,a.user_id,a.action_code,b.rsrv_value_code,b.rsrv_value*(-1),0,a.action_count,a.remark from tf_b_trade_scoresub a,tf_b_trade_other b
where a.trade_id=b.trade_id and b.rsrv_value !='0.00' and a.trade_id=:TRADE_ID and b.rsrv_str1=to_char(:RSRV_STR1)
and a.user_id=b.rsrv_str1