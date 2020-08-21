UPDATE tf_f_spanopen a
   SET (a.subscribe_state,a.success_moved_score) = 
   (SELECT '5', b.success_moved_score
   FROM tf_b_trade_spanopen b WHERE b.trade_id=TO_NUMBER(:TRADE_ID))
 WHERE a.booking_id=(SELECT c.booking_id FROM tf_b_trade_spanopen c WHERE c.trade_id=TO_NUMBER(:TRADE_ID))