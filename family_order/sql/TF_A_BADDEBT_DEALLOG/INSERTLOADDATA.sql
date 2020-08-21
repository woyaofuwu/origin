INSERT INTO tf_a_baddebt_deallog(acct_id,user_id,partition_id,serial_number,bill_id,acyc_id,bcyc_id,integrate_item_code,integrate_item,balance,created_date,deal_status,deal_date,deal_depart_id,deal_staff_id,baddebt_reason_id,remark,data_source)
 VALUES(TO_NUMBER(:ACCT_ID),TO_NUMBER(:USER_ID),TO_NUMBER(MOD(:ACCT_ID,10000)),:SERIAL_NUMBER,
TO_NUMBER(:BILL_ID),:ACYC_ID,:BCYC_ID,:INTEGRATE_ITEM_CODE,:INTEGRATE_ITEM,TO_NUMBER(:BALANCE),
SYSDATE,'1',SYSDATE,:DEAL_DEPART_ID,:DEAL_STAFF_ID,:BADDEBT_REASON_ID,:REMARK,'2')