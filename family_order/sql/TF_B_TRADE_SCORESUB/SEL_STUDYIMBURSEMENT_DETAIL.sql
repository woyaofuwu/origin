SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,action_code,score_type_code,to_char(score_changed_sub) score_changed_sub,to_char(value_changed_sub) value_changed_sub,action_count,remark 
  FROM tf_b_trade_scoresub a
 WHERE a.trade_id=TO_NUMBER(:TRADE_ID)
   AND a.accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND EXISTS (SELECT 1
                 FROM td_b_score_action b
                WHERE b.action_code=a.action_code
                AND b.exchange_type_code='9')