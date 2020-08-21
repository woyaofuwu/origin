SELECT bank_acct_no 
  FROM tf_f_account
 WHERE cust_id=TO_NUMBER(:CUST_ID)
 and remove_tag = '0'
 and pay_mode_code = '1'
 and rownum < 2