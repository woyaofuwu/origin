SELECT depart_id,
       to_char(depart_log_time,'yyyy-mm-dd hh24:mi:ss') depart_log_time,
       to_char(depart_log_time,'yyyymmdd')  print_info1,
       to_char(print_time,'yyyy-mm-dd hh24:mi:ss') print_time,
       to_char(trans_money) trans_money,
       to_char(trans_count) trans_count,
       print_staff_id,
       NVL(print_times,0)  print_times
  FROM tf_a_departprintlog
 WHERE depart_id=:DEPART_ID
   AND print_time>=TO_DATE(:SDEPART_LOG_TIME, 'YYYYMMDD')
   AND print_time<=TO_DATE(:EDEPART_LOG_TIME, 'YYYYMMDD')