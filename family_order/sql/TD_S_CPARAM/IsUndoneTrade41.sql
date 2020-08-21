SELECT TRADE_ID
  FROM tf_b_trade
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND exec_time>SYSDATE
   AND trade_type_code != '41'