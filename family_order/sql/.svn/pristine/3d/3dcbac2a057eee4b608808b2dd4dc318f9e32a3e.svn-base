SELECT to_char(trade_id) trade_id,to_char(user_id) user_id,to_char(SUM(score_changed)) score_changed 
  FROM tf_b_trade_score_new
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
GROUP BY trade_id,user_id
ORDER BY score_changed