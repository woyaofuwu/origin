UPDATE TF_F_USER_PURCHASE 
SET left_gdeposit=left_gdeposit - to_number(:LEFT_DEPOSIT),								gleft_months=gleft_months -1 WHERE USER_ID =TO_NUMBER(:USER_ID) AND PURCHASE_ATTR=:PURCHASE_ATTR AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
AND PROCESS_TAG = '0' and gleft_months > 0 and left_gdeposit > 0