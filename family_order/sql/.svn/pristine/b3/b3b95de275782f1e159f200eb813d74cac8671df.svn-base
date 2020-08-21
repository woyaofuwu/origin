select /*+ first_rows(1) index(t pk_tf_b_trade_attr) */count(*) recordcount
from   tf_b_trade_attr t, tf_f_user_attr u
where  u.rsrv_num1 = t.rsrv_num1
and    u.end_date > sysdate
and    u.attr_code = 'serv_para7'
and    t.attr_code = 'serv_para7'
and    u.attr_value = t.attr_value
and    u.user_id = t.user_id
and    u.partition_id = mod(t.user_id, 10000)
and    t.rsrv_num1 = :SERVICE_ID
and    (decode(t.modify_tag, '4', '0', '5', '1', t.modify_tag) = :MODIFY_TAG or :MODIFY_TAG = '*')
and    t.trade_id = to_number(:TRADE_ID)
and    t.accept_month = to_number(:ACCEPT_MONTH)
and    rownum < 2