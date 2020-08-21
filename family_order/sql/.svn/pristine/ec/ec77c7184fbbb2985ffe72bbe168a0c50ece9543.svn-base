select count(1) recordcount
         from   (select RSRV_NUM1 service_id, ATTR_VALUE serv_para2
         from   tf_f_user_attr a
         where  a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
         and    a.user_id = :USER_ID
         and    a.inst_type = 'S'
         and    a.attr_code = 'serv_para2'
         and    a.attr_value = 'open'
         and    a.rsrv_num1 = 102
         and    a.end_date > sysdate
         and    NOT EXISTS (select 1
                 from   tf_b_trade_attr b
                 where  b.trade_id = :TRADE_ID
                 AND    b.accept_month = to_number(substr(:TRADE_ID,5,2))
                 and    b.user_id = :USER_ID
                 and    b.inst_type = 'S'
                 and    b.attr_code = 'serv_para2'
                 and    b.attr_value = 'open'
                 and    b.rsrv_num1 = 102)
         UNION
         select RSRV_NUM1 service_id, ATTR_VALUE serv_para2
         from   tf_b_trade_attr c
         where  c.trade_id = :TRADE_ID
         AND    c.accept_month = to_number(substr(:TRADE_ID,5,2))
         and    c.user_id = :USER_ID
         and    c.inst_type = 'S'
				 and    c.attr_code = 'serv_para2'
         and    c.attr_value = 'open'
         and    c.rsrv_num1 = 102
         and    decode(c.modify_tag, '4', '0','5','1', c.modify_tag) <> '1'
         and    c.end_date > sysdate) d
       where   EXISTS (select 1   from
        (select RSRV_NUM1 service_id, ATTR_VALUE serv_para2
                 from   tf_f_user_attr e
                 where  e.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                 and    e.user_id = :USER_ID
                 and    e.inst_type = 'S'
                 and    e.RSRV_NUM1 = 1
                 and    e.end_date > sysdate
                 and    NOT EXISTS (select 1
                         from   tf_b_trade_attr f
                         where  f.trade_id = :TRADE_ID
                         AND    f.accept_month = to_number(substr(:TRADE_ID,5,2))
                         and    f.user_id = :USER_ID
                         and    f.inst_type = 'S'
                         and    f.RSRV_NUM1 = 1)
                 UNION
                 select RSRV_NUM1 service_id, ATTR_VALUE serv_para2
                 from   tf_b_trade_attr g
                 where  g.trade_id = :TRADE_ID
                 AND    g.accept_month = to_number(substr(:TRADE_ID,5,2))
                 and    g.user_id = :USER_ID
                 and    g.inst_type = 'S'
                 and    g.RSRV_NUM1 = 1
                 and    decode(g.modify_tag, '4', '0','5','1', g.modify_tag) <> '1'
                 and    g.end_date > sysdate
                 )
                 )