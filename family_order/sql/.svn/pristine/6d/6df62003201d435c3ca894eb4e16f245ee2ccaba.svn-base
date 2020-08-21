UPDATE td_s_task
   SET task_name=:TASK_NAME,plug_type=:PLUG_TYPE,plug_name=:PLUG_NAME,channel_id=to_number(:CHANNEL_ID),start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),use_tag=to_number(:USE_TAG),link_mode=:LINK_MODE,user_name=:USER_NAME,pass_code=:PASS_CODE,sid=:SID  
 WHERE task_id=:TASK_ID AND TASK_ID >= 300000 AND TASK_ID <= 399999