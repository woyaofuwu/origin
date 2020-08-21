select count(1) TRADE_COUNT
from tf_b_trade a,tf_b_trade_relation b
where a.trade_id = b.trade_id
and a.trade_type_code = '114'
and a.user_id = :USER_ID_A
and b.user_id_b = :USER_ID_B
and a.trade_id <> :TRADE_ID