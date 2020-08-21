SELECT to_char(sum(nvl(a.balance,0))) late_balance FROM tf_a_subconsignlog A,
TD_A_ITEMPRIORRULE B WHERE consign_id=:CONSIGN_ID AND 
a.INTEGRATE_ITEM_CODE = b.INTEGRATE_ITEM_CODE
and b.INTEGRATE_ITEM_CODE!= -100 and b.NOTOWETAG = '1'