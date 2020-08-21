SELECT COUNT(1) recordcount
  FROM tf_b_trade
 WHERE user_id = :USER_ID
   AND rownum < 2