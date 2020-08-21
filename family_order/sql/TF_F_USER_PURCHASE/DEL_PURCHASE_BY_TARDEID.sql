DELETE FROM tf_f_user_purchase
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND trade_id+0 = TO_NUMBER(:TRADE_ID)