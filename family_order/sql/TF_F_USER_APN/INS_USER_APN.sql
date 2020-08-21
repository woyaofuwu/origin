INSERT INTO tf_f_user_apn
(partition_id, user_id, service_id, para_code, para_type_code, ip_address, rsrv_str1, rsrv_str2, rsrv_str3, start_date, end_date, update_time)
VALUES
(to_number(:PARTITION_ID),  to_number(:USER_ID), :SERVICE_ID, :PARA_CODE, :PARA_TYPE_CODE, :IP_ADDRESS, :RSRV_STR1, :RSRV_STR2, :RSRV_STR3, to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss'), to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'), SYSDATE)