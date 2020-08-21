SELECT a.cust_id,nvl(a.pay_name,' ') pay_name,
 a.pay_mode_code,nvl(a.bank_code,0) bank_code,
 nvl(a.bank_acct_no,0) bank_acct_no, a.city_code city_code,
 nvl(b.bank,0) rsrv_str8,a.debuty_code,a.debuty_user_id,
 a.contract_no
 FROM tf_f_account a,td_b_bank b
 WHERE a.partition_id=MOD(TO_NUMBER(:ACCT_ID),10000) 
 AND a.acct_id = :ACCT_ID
 AND a.bank_code = b.bank_code(+)
 AND rownum < 2