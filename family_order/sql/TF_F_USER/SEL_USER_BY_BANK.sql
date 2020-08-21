SELECT b.serial_number
  FROM tf_f_account a,tf_f_user b
 WHERE a.bank_code = :BANK_CODE
   AND a.bank_acct_no = :BANK_ACCT_NO
   AND a.remove_tag = '0'
   AND a.cust_id+0 = b.cust_id
   AND b.remove_tag||NULL = '0'
   AND a.pay_mode_code NOT IN ('0','5')