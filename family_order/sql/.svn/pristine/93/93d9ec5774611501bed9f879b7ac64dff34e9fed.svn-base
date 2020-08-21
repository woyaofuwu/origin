UPDATE tf_f_spanopen a
   SET (a.subscribe_state,a.base_line,a.dead_line,a.need_move_score,a.reason) = 
   (SELECT decode(b.oper_type,'0','6','2'), nvl(b.base_line,a.base_line),nvl(b.dead_line,a.dead_line),decode(b.oper_type,'2','0','3','1',a.need_move_score),b.reason
   FROM tf_b_trade_spanopen b WHERE b.trade_id=TO_NUMBER(:TRADE_ID))
 WHERE a.booking_id=(SELECT c.booking_id FROM tf_b_trade_spanopen c WHERE c.trade_id=TO_NUMBER(:TRADE_ID))