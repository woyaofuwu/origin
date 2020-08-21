--IS_CACHE=Y
SELECT work_id,work_name,to_char(status) status,to_char(last_date,'yyyy-mm-dd hh24:mi:ss') last_date,to_char(this_date,'yyyy-mm-dd hh24:mi:ss') this_date,to_char(next_date,'yyyy-mm-dd hh24:mi:ss') next_date,dynamic_interval,alarm_tag,timeout,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(use_tag) use_tag
  FROM td_s_work
 WHERE work_id >= 300000 AND work_id <= 399999