UPDATE tf_f_user_purchase 
  SET end_date=to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
    WHERE user_id = TO_NUMBER(:USER_ID)
        AND trade_id+0 = TO_NUMBER(:TRADE_ID)