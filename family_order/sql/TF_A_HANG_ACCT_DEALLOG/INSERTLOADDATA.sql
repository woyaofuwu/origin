INSERT INTO tf_a_hang_acct_deallog(acct_id,user_id,partition_id,serial_number,bill_id,acyc_id,bcyc_id,integrate_item_code,integrate_item,balance,created_date,deal_status,hang_acct_date,depart_id,staff_id,hang_acct_reason,remark1,data_source)
 VALUES(TO_NUMBER(:ACCT_ID),TO_NUMBER(:USER_ID),TO_NUMBER(MOD(:ACCT_ID,10000)),:SERIAL_NUMBER,
TO_NUMBER(:BILL_ID),:ACYC_ID,:BCYC_ID,:INTEGRATE_ITEM_CODE,:INTEGRATE_ITEM,TO_NUMBER(:BALANCE),
SYSDATE,'1',SYSDATE,:DEPART_ID,:STAFF_ID,:HANG_ACCT_REASON,:REMARK1,'2')