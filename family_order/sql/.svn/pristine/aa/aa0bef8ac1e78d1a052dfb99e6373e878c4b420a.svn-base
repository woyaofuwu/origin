SELECT eparchy_code,to_char(adjust_id) adjust_id,to_char(user_id) user_id,partition_id,to_char(bill_id) bill_id,acyc_id,adjust_type,adjust_mode,to_char(adjust_fee) adjust_fee,to_char(adjust_per) adjust_per,to_char(adjust_time,'yyyy-mm-dd hh24:mi:ss') adjust_time,adjust_eparchy_code,adjust_city_code,adjust_depart_id,adjust_staff_id,adjust_reason_code,remark,to_char(trade_id) trade_id,operate_type,serial_number,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10 
  FROM tf_a_adjustalog a
  where ( a.serial_number =to_char(:SERIAL_NUMBER) OR :SERIAL_NUMBER IS NULL)
   and ( a.adjust_staff_id >=to_char(:X_START_STAFF_ID) OR :X_START_STAFF_ID IS NULL )
  and ( a.adjust_staff_id <= to_char(:X_END_STAFF_ID) OR :X_END_STAFF_ID IS NULL  )
   and ( a.adjust_time >= to_date(:START_DATE,'yyyy-mm-dd') OR :START_DATE IS NULL  ) 
   and ( a.adjust_time <= to_date(:END_DATE,'yyyy-mm-dd') OR :END_DATE IS NULL )
   and (  a.adjust_type= to_char(:ADJUST_TYPE) OR :ADJUST_TYPE IS NULL )