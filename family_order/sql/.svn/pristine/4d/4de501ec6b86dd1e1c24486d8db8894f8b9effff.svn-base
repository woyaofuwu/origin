--IS_CACHE=Y
SELECT work_id,start_task_id,end_task_id,to_char(status) status,process_id,to_char(start_exec_time,'yyyy-mm-dd hh24:mi:ss') start_exec_time,to_char(end_exec_time,'yyyy-mm-dd hh24:mi:ss') end_exec_time,timeout_flag,timeout_interval,timeout_fault_interval,timeout_action_code,fault_action_code,fault_redoctrl_number,fault_nowredoctrl_number,to_char(fault_lastredo_date,'yyyy-mm-dd hh24:mi:ss') fault_lastredo_date,fault_redo_interval,result_info,result_code 
  FROM td_s_route
 WHERE work_id >= 300000 AND work_id <= 399999