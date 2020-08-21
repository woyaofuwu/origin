INSERT INTO TF_F_USER_BILLPRINT(partition_id,user_id,month,bill_type,print_count,print_date) 
VALUES(MOD(TO_NUMBER(:USER_ID),10000),TO_NUMBER(:USER_ID),:MONTH,:BILL_TYPE,:PRINT_COUNT,sysdate)