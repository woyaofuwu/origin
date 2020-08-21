select  log_id,fee_type_code,
 fee_money,
 to_char(reg_date,'yyyy-mm-dd hh24:mi:ss')reg_date,
 reg_staff_id,
 reg_depart_id,
 state,
 to_char(update_time,'yyyy-mm-dd hh24:mi:ss')update_time,
 update_staff_id,
 update_depart_id,
 remark
 from tf_F_feereg
 where log_id=:LOG_ID
 and fee_type_code = 'T1'