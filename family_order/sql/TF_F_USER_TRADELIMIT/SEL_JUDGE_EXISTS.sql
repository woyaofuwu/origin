SELECT to_char(user_id) user_id,trade_type_code 
  FROM tf_f_user_tradelimit
WHERE user_id=TO_NUMBER(:USER_ID)
   AND trade_type_code=:TRADE_TYPE_CODE
   AND sysdate BETWEEN start_date+0 AND end_date+0