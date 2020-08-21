SELECT COUNT(1) recordcount
  FROM tf_f_user
 WHERE user_id =:USER_ID
   AND eparchy_code <> :TRADE_EPARCHY_CODE