SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,action_code,score_type_code,to_char(score_changed_sub) score_changed_sub,to_char(value_changed_sub) value_changed_sub,action_count,remark 
  FROM tf_b_trade_scoresub
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND (user_id=TO_NUMBER(:USER_ID) OR :USER_ID IS NULL)