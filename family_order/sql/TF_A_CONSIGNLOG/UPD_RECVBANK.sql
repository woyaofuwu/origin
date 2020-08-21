UPDATE tf_a_consignlog
SET recv_bank_acct_no=:RECV_BANK_ACCT_NO , 
recv_bank_code=:RECV_BANK_CODE,
recv_bank=:RECV_BANK,
recv_name=:RECV_NAME
WHERE consign_id=TO_NUMBER(:CONSIGN_ID)