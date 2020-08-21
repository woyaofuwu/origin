DELETE FROM td_s_alarm
 WHERE work_id=:WORK_ID
   AND alarm_id=:ALARM_ID
   AND work_id >= 300000
   AND work_id <= 399999