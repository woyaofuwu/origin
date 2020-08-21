SELECT a.deal_id,
       a.serial_number,
       a.accept_date,
       a.biz_type,
       a.monitor_flag,
       a.monitor_rule_code,
       a.accept_staff_id,
       a.accept_depart_id,
       a.accept_mode,
       a.write_type,
       a.enable_tag,
       a.start_date,
       a.end_date,
       a.deal_state,
       a.deal_time,
       a.deal_result,
       a.trade_id,
       a.batch_id,
       a.eparchy_code
  FROM tf_b_ocs_batdeal a
 WHERE 1 = 1
   AND a.batch_id = :BATCH_ID
   AND a.serial_number = :SERIAL_NUMBER
   AND a.accept_date >= to_date(:START_DATE, 'YYYY-MM-DD')
   AND a.accept_date < to_date(:END_DATE, 'YYYY-MM-DD') + 1
   AND a.accept_mode = '1'
   AND a.biz_type = :BIZ_TYPE
UNION
SELECT b.deal_id,
       b.serial_number,
       b.accept_date,
       b.biz_type,
       b.monitor_flag,
       b.monitor_rule_code,
       b.accept_staff_id,
       b.accept_depart_id,
       b.accept_mode,
       b.write_type,
       b.enable_tag,
       b.start_date,
       b.end_date,
       b.deal_state,
       b.deal_time,
       b.deal_result,
       b.trade_id,
       b.batch_id,
       b.eparchy_code
  FROM tf_bh_ocs_batdeal b
 WHERE 1 = 1
   AND b.batch_id = :BATCH_ID
   AND b.serial_number = :SERIAL_NUMBER
   AND b.accept_date >= to_date(:START_DATE, 'YYYY-MM-DD')
   AND b.accept_date < to_date(:END_DATE, 'YYYY-MM-DD') + 1
   AND b.accept_mode = '1'
   AND b.biz_type = :BIZ_TYPE
