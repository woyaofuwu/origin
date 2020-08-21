UPDATE tf_a_account_bak
   SET pay_name=:PAY_NAME,bank_code=:BANK_CODE,bank_acct_no=:BANK_ACCT_NO,rsrv_str2=:RSRV_STR2,rsrv_str3=:RSRV_STR3,rsrv_str4=:RSRV_STR4,rsrv_str5=:RSRV_STR5,rsrv_str6=:RSRV_STR6,rsrv_str7=:RSRV_STR7,rsrv_str8=:RSRV_STR8,rsrv_str9=:RSRV_STR9,rsrv_str10=:RSRV_STR10,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE partition_id=:PARTITION_ID
   AND acct_id=TO_NUMBER(:ACCT_ID)