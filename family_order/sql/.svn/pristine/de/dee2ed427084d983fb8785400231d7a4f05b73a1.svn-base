UPDATE tf_f_spanopen
   SET subscribe_state = '4'
 WHERE booking_id=(SELECT booking_id FROM tf_b_trade_spanopen WHERE trade_id=TO_NUMBER(:TRADE_ID))