UPDATE tf_b_tradefee_paymoney
   SET money=TO_NUMBER(:MONEY)  
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND accept_month=:ACCEPT_MONTH
   AND pay_money_code=:PAY_MONEY_CODE