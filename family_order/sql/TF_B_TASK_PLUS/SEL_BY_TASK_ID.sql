SELECT TASK_ID,PARTITION_ID,SEND_OBJECT,INFO_ID,INFO_VALUE,INFO_DESC,MONTH 
FROM TF_B_TASK_PLUS WHERE 1=1 AND TASK_ID = :TASK_ID AND PARTITION_ID = :PARTITION_ID ORDER BY TASK_ID,SEND_OBJECT