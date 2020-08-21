SELECT a.user_id,a.cust_id,a.serial_number,b.acct_id FROM tf_f_user a, tf_f_account b
WHERE a.cust_id=b.cust_id
AND a.user_id=:USER_ID
AND b.remove_tag='0'