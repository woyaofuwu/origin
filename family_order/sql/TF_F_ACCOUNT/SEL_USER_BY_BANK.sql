SELECT b.serial_number,a.pay_name,a.acct_id,a.pay_mode_code,a.rsrv_str3 post_code,a.rsrv_str10 post_address,a.contract_no
  FROM tf_f_account a,tf_f_user b
 WHERE 1=1 and a.bank_code = :BANK_CODE
 	AND a.remove_tag = '0'
   AND a.bank_acct_no = :BANK_ACCT_NO
   AND a.cust_id+0 = b.cust_id
	AND b.remove_tag||NULL = '0'
	AND a.pay_mode_code NOT IN ('0','5')