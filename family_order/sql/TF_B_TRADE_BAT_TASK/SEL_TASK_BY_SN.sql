SELECT b.batch_task_id,b.batch_task_name,b.batch_oper_name,b.start_date,b.end_date,b.create_staff_id,b.create_time,b.audit_no,b.remark
FROM tf_b_trade_batdeal a, tf_b_trade_bat_task b
WHERE 1=1
AND a.batch_task_id = b.batch_task_id
AND a.serial_number = :SERIAL_NUMBER