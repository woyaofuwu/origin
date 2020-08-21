UPDATE td_s_work
   SET work_name=:WORK_NAME,next_date=TO_DATE(:NEXT_DATE, 'YYYY-MM-DD HH24:MI:SS'),dynamic_interval=:DYNAMIC_INTERVAL,alarm_tag=:ALARM_TAG,timeout=:TIMEOUT,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),use_tag=to_number(:USE_TAG)
 WHERE work_id=:WORK_ID AND WORK_ID >= 300000 AND WORK_ID <= 399999