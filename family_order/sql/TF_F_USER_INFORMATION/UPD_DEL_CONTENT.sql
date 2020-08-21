UPDATE tf_f_user_information
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)