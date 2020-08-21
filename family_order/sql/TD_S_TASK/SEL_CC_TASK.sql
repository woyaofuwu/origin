--IS_CACHE=Y
SELECT task_id,task_name,plug_type,plug_name,to_char(channel_id) channel_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(use_tag) use_tag,link_mode,user_name,pass_code,sid 
  FROM td_s_task
 WHERE task_id >= 300000 AND task_id <= 399999