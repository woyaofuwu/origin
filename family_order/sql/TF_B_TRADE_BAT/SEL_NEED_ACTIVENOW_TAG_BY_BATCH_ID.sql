SELECT b.need_activenow_tag
FROM tf_b_trade_bat a, td_b_batchtype b
WHERE a.batch_oper_type = b.batch_oper_type
AND a.batch_id = :BATCH_ID