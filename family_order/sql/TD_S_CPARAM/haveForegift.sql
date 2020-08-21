SELECT COUNT(1) recordcount
  FROM tf_f_user_foregift
 WHERE foregift_code = '8'
   AND money > 0
   AND user_id = :USER_ID