select count(1) TRADE_COUNT
from tf_b_trade a,tf_b_trade_discnt b
where a.trade_id = b.trade_id
and a.trade_type_code = '114'
and a.user_id = :USER_ID
and a.trade_id <> :TRADE_ID
and b.discnt_code in
(select discnt_code from tf_b_trade_discnt c where c.trade_id = :TRADE_ID)