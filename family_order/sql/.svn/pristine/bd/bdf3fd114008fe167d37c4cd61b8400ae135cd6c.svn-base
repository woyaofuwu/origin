SELECT to_char(trade_id) trade_id,accept_month,pay_money_code,to_char(money) money 
  FROM tf_b_tradefee_paymoney
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))