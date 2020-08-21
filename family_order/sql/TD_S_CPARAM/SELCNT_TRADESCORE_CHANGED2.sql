select nvl(a.score_value,0) + SCORE_CHANGED  RECORDCOUNT from tf_f_user a,
(
select nvl(sum(SCORE_CHANGED),0) SCORE_CHANGED
  from tf_b_trade_score
 where trade_id = :TRADE_ID
   and user_id = :USER_ID
)b
where user_id = :USER_ID
   and a.partition_id=mod(:USER_ID,10000)