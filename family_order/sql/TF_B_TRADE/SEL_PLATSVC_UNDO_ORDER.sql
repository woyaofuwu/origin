Select Count(1) ORDERNUM From tf_B_trade a
 Where  a.user_id = :USER_ID
 And a.trade_type_code ='3700'
 AND EXISTS (SELECT  1  FROM  tf_b_trade_platsvc b WHERE a.trade_id =  b.trade_id  And b.service_id  =  :SERVICE_ID)