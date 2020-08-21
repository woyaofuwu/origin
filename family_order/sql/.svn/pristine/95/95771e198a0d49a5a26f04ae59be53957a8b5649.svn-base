DELETE FROM tf_b_trade_span_apply_detail
 WHERE apply_id_sub_1 IN 
 (SELECT apply_id_sub FROM tf_b_trade_span_apply_sub WHERE apply_id_1 = :APPLY_ID)