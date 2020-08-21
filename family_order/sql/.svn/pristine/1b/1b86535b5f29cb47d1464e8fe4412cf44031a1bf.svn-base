select nvl(sum(SCORE_CHANGED),0) RECORDCOUNT
  from tf_b_trade_score
 where trade_id = :TRADE_ID
   and user_id = :USER_ID