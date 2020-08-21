SELECT b.batch_task_name,
       b.batch_oper_name,
       b.create_staff_id,
       b.create_time,
       to_char(b.start_date,'yyyy-mm-dd HH24:MI:SS') start_date,
       b.total_count,
       b.finish_count,
       b.finish_count/b.total_count * 100||'%' per
  FROM TF_B_BAT_STAT b
 WHERE   b.batch_task_name= :BATCH_TASK_NAME OR  :BATCH_TASK_NAME IS NULL
 AND (:BATCH_OPER_CODE IS NULL OR b.batch_oper_code=:BATCH_OPER_CODE)
 AND (:START_DATE IS NULL  OR start_date+0 >=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
 AND (:END_DATE IS NULL  OR start_date+0 <=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))
