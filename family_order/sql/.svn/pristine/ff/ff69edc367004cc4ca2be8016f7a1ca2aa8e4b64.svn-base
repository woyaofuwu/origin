SELECT trade_id,accept_month,user_id,serial_number,score,-score_changed score_changed,value_changed,remark 
  FROM tf_b_trade_score
 WHERE trade_id=:TRADE_ID
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))