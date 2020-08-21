SELECT COUNT(1) recordcount
  FROM dual
 WHERE
(SELECT SUM(money)
   FROM tf_f_user_foregift
  WHERE user_id=:USER_ID
    AND foregift_code=to_number(:FOREGIFT_CODE))>=TO_NUMBER(:NUM)