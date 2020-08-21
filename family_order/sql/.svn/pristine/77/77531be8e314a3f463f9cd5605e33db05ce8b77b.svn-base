SELECT to_char(trade_id) trade_id,accept_month,pay_money_code,to_char(money) money 
  FROM tf_b_tradefee_paymoney
 WHERE order_id=TO_NUMBER(:ORDER_ID)
and accept_month = TO_NUMBER(:ACCEPT_MONTH)