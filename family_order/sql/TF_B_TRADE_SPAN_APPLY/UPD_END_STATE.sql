UPDATE tf_b_trade_span_apply
   SET end_date=sysdate 
 WHERE apply_id=:APPLY_ID
   AND apply_state=:APPLY_STATE