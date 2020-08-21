Select Count(1) ORDERNUM From tf_B_trade a,tf_b_trade_platsvc b
 Where a.trade_id =  b.trade_id
 And a.user_id = :USER_ID
 And a.trade_type_code IN ('3700','3788')
 AND b.oper_code IN ('18','19')