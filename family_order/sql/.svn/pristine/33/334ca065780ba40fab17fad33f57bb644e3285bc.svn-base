SELECT serial_number,user_id,start_time,end_time,remark 
FROM TF_F_SMS_REDMEMBER
WHERE 1=1
AND user_id = :USER_ID
AND SYSDATE BETWEEN start_time AND end_time