UPDATE td_s_route
   SET status=to_number(:STATUS),timeout_flag=:TIMEOUT_FLAG,timeout_interval=:TIMEOUT_INTERVAL,timeout_fault_interval=:TIMEOUT_FAULT_INTERVAL,timeout_action_code=:TIMEOUT_ACTION_CODE,fault_action_code=:FAULT_ACTION_CODE,fault_redoctrl_number=:FAULT_REDOCTRL_NUMBER,fault_redo_interval=:FAULT_REDO_INTERVAL  
 WHERE work_id=:WORK_ID
   AND start_task_id=:START_TASK_ID
   AND end_task_id=:END_TASK_ID
   AND WORK_ID >= 300000
   AND WORK_ID <= 399999