INSERT  INTO tl_o_inspectionlog
  (task_id,
   applyid,
   tasktype,
   searchnumber,
   starttime,
   endtime,
   letternum,
   stateupdatetime1,
   deal_times,
   process_tag,
   exec_time) 
SELECT :TASK_ID,
         :APPLYID,
         :TASKTYPE,
         :SEARCHNUMBER,
         to_date(:STARTTIME, 'YYYY-MM-DD HH24:MI:SS'),
         to_date(:ENDTIME, 'YYYY-MM-DD HH24:MI:SS'),
         :LETTERNUM,
         to_date(:STATEUPDATETIME1, 'YYYY-MM-DD HH24:MI:SS'),
:DEAL_TIMES,
:PROCESS_TAG,
to_date(:EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS')
FROM dual
WHERE NOT EXISTS (SELECT 1
FROM TL_O_INSPECTIONLOG
WHERE 1 = 1
and TASK_ID = :TASK_ID)