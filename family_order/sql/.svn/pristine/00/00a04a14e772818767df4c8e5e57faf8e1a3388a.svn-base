UPDATE tf_f_user_tradelimit
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND trade_type_code=:TRADE_TYPE_CODE