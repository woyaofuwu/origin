update tf_a_payrelation a
set a.acct_id = :ACCT_ID
where user_id in ( 
select b.id_b  
from tf_b_trade_relation b
where  (trade_id = :TRADE_ID and 
modify_tag = '1') 
)