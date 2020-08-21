SELECT to_char(a.id) id,a.id_type,a.partition_id,a.effect_item_code,a.act_mode,
a.effect_mode,a.addup_mode,a.effect_value_type,to_char(a.effect_value) effect_value,to_char(a.refer_fee) refer_fee,
a.start_acyc_id,a.end_acyc_id,to_char(a.remainfee) remainfee,to_char(a.use_fee1) use_fee1,to_char(a.use_fee2) use_fee2,
to_char(a.limit_fee) limit_fee,a.recv_tag,a.adjust_reason_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
a.adjust_depart_id,a.adjust_staff_id,a.adjust_eparchy_code, b.serial_number bill_id
  FROM tf_a_adjustbbill a, tf_f_user b
 WHERE a.id=b.user_id AND b.remove_tag='0' 
AND (a.start_acyc_id >= :START_ACYC_ID) 
AND (a.end_acyc_id <= :END_ACYC_ID)
AND ( b.serial_number >= :START_SERIAL_NUMBER  OR :START_SERIAL_NUMBER IS NULL ) 
AND ( b.serial_number <= :END_SERIAL_NUMBER  OR :END_SERIAL_NUMBER IS NULL) 
AND ( a.adjust_staff_id >= :X_START_STAFF_ID OR :X_START_STAFF_ID IS NULL  )
AND ( a.adjust_staff_id <= :X_END_STAFF_ID  OR :X_END_STAFF_ID IS NULL )