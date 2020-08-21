SELECT COUNT(1) recordcount
  FROM tf_f_user_foregift
 WHERE user_id = :USER_ID
   AND money > 0