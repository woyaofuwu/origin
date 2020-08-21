 select  t.* 
     from tf_b_trade t, td_s_commpara a
    where a.para_code1 = t.rsrv_str2
      and t.trade_type_code = '240'
      and t.cancel_tag = '2'
      and a.param_attr in (6895, 6898)
			and t.trade_id=:TRADE_ID