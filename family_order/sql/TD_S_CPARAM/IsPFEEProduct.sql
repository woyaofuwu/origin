select count(1) recordcount
from   tf_b_trade_other
where  trade_id = to_number(:TRADE_ID)
and    accept_month = to_number(substr(:TRADE_ID, 5,2))
and    rsrv_value_code = 'PFEE'
and    rsrv_str9 = :RSRV_STR9