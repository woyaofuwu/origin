select a.User_id 
  from tf_b_trade a,tf_b_trade_score b
 where a.trade_id = b.trade_id
   and a.cancel_tag = :CANCEL_TAG
   and b.score_changed < '0'
   and a.user_id = :USER_ID