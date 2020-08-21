select a.* from tf_b_trade_discnt a,td_s_commpara b where a.trade_id = :TRADE_ID and b.subsys_code='CSM'
                AND b.param_attr='2012' and b.param_code = 'musicdiscnt' and a.discnt_code = b.para_code1
                and (a.end_date between to_date('2012/7/1','yyyy/mm/dd') and to_date('2012/12/31','yyyy/mm/dd'))