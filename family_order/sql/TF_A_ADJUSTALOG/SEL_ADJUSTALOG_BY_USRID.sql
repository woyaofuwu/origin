SELECT eparchy_code,to_char(adjust_id) adjust_id,to_char(user_id) user_id,partition_id,to_char(bill_id) bill_id,acyc_id,adjust_type,adjust_mode,to_char(adjust_fee) adjust_fee,to_char(adjust_per) adjust_per,to_char(adjust_time,'yyyy-mm-dd hh24:mi:ss') adjust_time,adjust_eparchy_code,adjust_city_code,adjust_depart_id,adjust_staff_id,adjust_reason_code,remark,to_char(trade_id) trade_id,operate_type,serial_number,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10 
  FROM tf_a_adjustalog
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=:PARTITION_ID
   AND acyc_id=:ACYC_ID