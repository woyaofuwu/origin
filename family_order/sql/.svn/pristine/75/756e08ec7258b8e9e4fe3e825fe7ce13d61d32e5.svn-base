select count(*)  RECORDCOUNT
from   tf_b_trade_svc s, tf_b_trade_attr a
where  a.trade_id = s.trade_id
and    a.trade_id = :TRADE_ID
and    a.accept_month = :ACCEPT_MONTH
and    a.accept_month = s.accept_month
and    s.service_id = :SERVICE_ID
and    (decode(s.modify_tag, '4', '0','5','1', s.modify_tag) = :MODIFY_TAG or :MODIFY_TAG = '*')
and    a.inst_type = 'S'
and    a.inst_id = s.inst_id
and    a.attr_code = 'ip'
and    exists (select 1
        from   tf_b_trade_attr a2, tf_b_trade_svc s2
        where  a2.trade_id = s2.trade_id and a2.trade_id != :TRADE_ID
        and    s2.service_id = :SERVICE_ID
        and    a2.accept_month = s2.accept_month
        and    a2.inst_type = 'S'
        and    a2.inst_id = s.inst_id
        and    a2.attr_code = 'ip'
        and    a2.attr_value = a.attr_value)
and    rownum < 2