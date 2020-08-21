select to_char(trans_id) trans_id,
       user_id,
       serial_number,
       serial_number_temp,
       eparchy_code,
       state,
       TO_CHAR(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       TO_CHAR(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       TO_CHAR(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       remark,
       '1' HIS ,
       rsrv_str6,
       rsrv_str7
  from TF_F_SELFHELPCARD_FLOW
  WHERE (SERIAL_NUMBER=:SERIAL_NUMBER or :SERIAL_NUMBER is null)
  and (SERIAL_NUMBER_TEMP=:SERIAL_NUMBER_TEMP or :SERIAL_NUMBER_TEMP is null)
  
  UNION ALL
  
  select to_char(trans_id) trans_id,
       user_id,
       serial_number,
       serial_number_temp,
       eparchy_code,
       state,
       TO_CHAR(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       TO_CHAR(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       TO_CHAR(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       remark,
       '0' HIS ,
       rsrv_str6,
       rsrv_str7
  from TF_FH_SELFHELPCARD_FLOW
  WHERE (SERIAL_NUMBER=:SERIAL_NUMBER or :SERIAL_NUMBER is null)
  and (SERIAL_NUMBER_TEMP=:SERIAL_NUMBER_TEMP or :SERIAL_NUMBER_TEMP is null)