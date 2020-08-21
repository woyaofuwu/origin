select COUNT(1) recordcount
from tf_b_trade_vpn a,tf_f_user_vpn b
where trade_id=:TRADE_ID
 and a.short_code_len!=b.short_code_len
 and a.user_id=b.user_id