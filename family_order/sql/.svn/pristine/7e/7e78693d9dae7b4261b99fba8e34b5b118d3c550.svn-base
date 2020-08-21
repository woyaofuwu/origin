Select t.* 
  From tf_f_user t, tf_f_customer c
 where t.cust_id = c.cust_id
   and t.serial_number = :SERIAL_NUMBER
   and t.remove_tag = '0'
   and (((c.is_real_name != '1' or c.is_real_name is null) and t.acct_tag != '0') or
       ((c.is_real_name != '1' or c.is_real_name is null) and t.acct_tag = '0' and
       t.open_date >=
       TO_DATE('2013-09-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')))