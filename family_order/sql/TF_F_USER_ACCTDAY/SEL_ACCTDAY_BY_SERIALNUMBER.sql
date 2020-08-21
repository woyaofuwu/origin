SELECT acct_day 
FROM tf_f_user u ,tf_f_user_acctday a 
WHERE u.user_id = a.user_id AND SYSDATE BETWEEN a.start_date AND a.end_date AND u.serial_number =:SERIAL_NUMBER