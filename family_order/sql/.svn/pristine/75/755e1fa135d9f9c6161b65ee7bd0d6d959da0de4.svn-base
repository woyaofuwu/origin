UPDATE tf_b_trade_discnt
   SET modify_tag=:MODIFY_TAG  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=:ACCEPT_MONTH
   AND discnt_code=:DISCNT_CODE
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')