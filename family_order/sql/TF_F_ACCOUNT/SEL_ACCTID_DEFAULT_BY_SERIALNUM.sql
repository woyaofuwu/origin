select a.cust_id,a.eparchy_code,a.city_code,c.acct_id  from  tf_f_user a, tf_a_payrelation b, tf_f_account c
WHERE a.user_id = b.user_id
AND b.acct_id = c.acct_id
AND b.default_tag = '1'
AND a.serial_number = :SERIAL_NUMBER
AND a.remove_tag = '0' 
and TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')) between b.start_cycle_id and b.end_cycle_id