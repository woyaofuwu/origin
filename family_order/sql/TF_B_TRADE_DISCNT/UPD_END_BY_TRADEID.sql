UPDATE tf_b_trade_discnt
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=:ACCEPT_MONTH
   AND id=TO_NUMBER(:ID)
   AND discnt_code=:DISCNT_CODE