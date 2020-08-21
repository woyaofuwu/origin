select distinct up.product_id, acct.acct_id
  from tf_a_payrelation  pay,
       tf_f_account      acct,
       tf_f_user_product up,
       tf_f_user         u
 where up.product_mode = '12'
   and up.user_id = pay.user_id
   and up.partition_id = mod(pay.user_id, 10000)
   and up.end_date > sysdate
   and u.user_id = pay.user_id
   and u.partition_id = mod(pay.user_id, 10000)
   and u.remove_tag = '0'
   and to_number(to_char(SYSDATE, 'yyyymmdd')) between
       pay.start_cycle_id and pay.end_cycle_id
   and pay.default_tag = '0'
   and pay.act_tag = '1'
   and pay.acct_id = acct.acct_id
   and acct.remove_tag = '0'
   and acct.acct_id = :ACCT_ID
   and acct.cust_id = :CUST_ID
