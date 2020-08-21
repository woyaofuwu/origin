UPDATE td_s_alarm
   SET interval=:INTERVAL,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),serial_number=:SERIAL_NUMBER,use_tag=:USE_TAG  
 WHERE work_id=:WORK_ID
   AND alarm_id=:ALARM_ID
   AND work_id >= 300000
   AND work_id <= 399999