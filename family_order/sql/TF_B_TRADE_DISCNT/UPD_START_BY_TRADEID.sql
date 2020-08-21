UPDATE tf_b_trade_discnt
   SET start_date=TO_DATE(:NEW_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=:ACCEPT_MONTH
   AND user_id=TO_NUMBER(:USER_ID)
   AND discnt_code=:DISCNT_CODE
   AND modify_tag=:MODIFY_TAG
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')