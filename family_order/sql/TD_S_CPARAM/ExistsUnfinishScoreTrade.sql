SELECT COUNT(1) recordcount
  FROM tf_b_trade a
 WHERE user_id=:USER_ID
   AND cancel_tag=:CANCEL_TAG
   AND exists(SELECT 1 FROM TF_B_TRADE_SCORE
       WHERE trade_id=a.trade_id AND user_id=a.user_id)