UPDATE tf_b_trade
   SET cancel_tag=:CANCEL_TAG,exec_time=TO_DATE(:EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS'),cancel_date=TO_DATE(:CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS')
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND cancel_tag = '0'