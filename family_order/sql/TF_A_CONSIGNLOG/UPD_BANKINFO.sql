UPDATE tf_a_consignlog
SET bank_acct_no=:BANK_ACCT_NO, 
bank_code=:BANK_CODE,
bank=:BANK,
pay_name=:PAY_NAME
WHERE consign_id=TO_NUMBER(:CONSIGN_ID)