SELECT depart_id,to_char(depart_log_time,'yyyy-mm-dd hh24:mi:ss') depart_log_time,to_char(trans_money) trans_money,to_char(report_money) report_money,to_char(trans_count) trans_count,to_char(print_time,'yyyy-mm-dd hh24:mi:ss') print_time,print_eparchy_code,print_city_code,print_depart_id,print_staff_id,print_times,print_type,print_info1,to_char(print_number1) print_number1 
  FROM tf_a_departprintlog
 WHERE depart_id=:DEPART_ID
   AND depart_log_time>=TO_DATE(:SDEPART_LOG_TIME, 'YYYYMMDD')
   AND depart_log_time<=TO_DATE(:EDEPART_LOG_TIME, 'YYYYMMDD')