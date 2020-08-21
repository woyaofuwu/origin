select :CONSIGN_ID mconsign_id,integrate_item_code,integrate_item,
 to_char(balance) balance,to_char(late_balance) late_balance  
 from tf_a_subconsignlog
 where consign_id=:CONSIGN_ID order by integrate_item_code