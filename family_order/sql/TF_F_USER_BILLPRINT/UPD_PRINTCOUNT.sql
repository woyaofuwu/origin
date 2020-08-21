UPDATE TF_F_USER_BILLPRINT SET print_count=:PRINT_COUNT,print_date = sysdate
WHERE user_id=TO_NUMBER(:USER_ID) 
AND month = :MONTH 
AND bill_type=:BILL_TYPE