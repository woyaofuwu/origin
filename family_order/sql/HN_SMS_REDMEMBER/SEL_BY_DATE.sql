SELECT serial_number,user_id,start_time,end_time,remark 
FROM TF_F_SMS_REDMEMBER
WHERE 1=1
AND (:START_TIME IS NULL OR :START_TIME = '' OR start_time >= to_date(:START_TIME, 'yyyy-mm-dd')) 
AND (:END_TIME IS NULL OR :END_TIME = '' OR start_time < to_date(:END_TIME,'yyyy-mm-dd')+1)