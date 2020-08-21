SELECT serial_number,user_id,start_time,end_time,remark 
FROM TF_F_SMS_REDMEMBER
WHERE 1=1
AND serial_number = :SERIAL_NUMBER