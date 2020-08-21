SELECT count(1) recordcount
  FROM tf_f_account
 WHERE bank_acct_no = :BANK_ACCT_NO
   --AND bank_code = :BANK_CODE 
   AND pay_mode_code not in ('0','5')
   --AND pay_mode_code = :PAY_MODE_CODE
   AND acct_id != :ACCT_ID
   AND pay_name != :PAY_NAME
   AND remove_tag = '0'